package com.weburg.ghowst;

import jdk.jfr.Description;
import jdk.jfr.Name;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;

import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.logging.Logger;

public class HttpWebServiceMapper {
    private Object webService;
    private Class webServiceClass;
    private String uri;
    private WebService webServiceMetadata = new WebService();

    private Map<String, List<Class>> methodMap = new TreeMap<>();
    private Set<String> customTypes = new TreeSet<>();
    private String serviceDescription;
    private Map<String, String> resourceKeyNames = new HashMap<>();

    private static final Logger LOGGER = Logger.getLogger(HttpWebServiceMapper.class.getName());

    public HttpWebServiceMapper(Object webService, String uri) {
        this.webService = webService;
        this.webServiceClass = webService.getClass().getInterfaces()[0];
        this.uri = uri;
        this.serviceDescription = this.describeService(); // Populate before any invocations are made
    }

    public enum HttpMethod {
        GET, PUT, POST, PATCH, DELETE, OPTIONS
    }

    static class WebService {
        public String name;
        public String description;
        public String uri;
        public Set<ServiceMethod> methods;
        public Set<CustomType> customTypes;
        public Set<Resource> resources;

        static class ServiceMethod {
            public String name;
            public String description;
            public Set<MethodParameter> parameters;
            public MethodReturn returns;
            public String uri;

            static class MethodParameter {
                public String name;
                public String type;
                //public String description;
            }

            static class MethodReturn {
                public String type;
                //public String description;
            }
        }

        static class CustomType {
            public String name;
            public String description;
            public Set<Property> properties;

            static class Property {
                public String name;
                public String type;
                public String description;
            }
        }

        static class Resource {
            public String name;
            //public String description;
            public String uri;
            public Set<HttpMethod> allowMethods;
        }
    }

    public String getServiceDescription() {
        return this.serviceDescription;
    }

    public String getResourceKeyName(String resourceKey) {
        return this.resourceKeyNames.get(resourceKey);
    }

    public static String getResourceFromPath(String pathInfo) {
        if (pathInfo == null) {
            return "";
        }

        String[] pathParts = pathInfo.split("/");

        if (pathParts.length > 1) {
            return pathParts[1];
        } else {
            return "";
        }
    }

    public static String getResourceFromMethod(String methodName) {
        if (methodName.startsWith("get")) {
            methodName = methodName.substring("get".length());
            return methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
        } else if (methodName.startsWith("createOrReplace")) {
            methodName = methodName.substring("createOrReplace".length());
            return methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
        } else if (methodName.startsWith("create")) {
            methodName = methodName.substring("create".length());
            return methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
        } else if (methodName.startsWith("update")) {
            methodName = methodName.substring("update".length());
            return methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
        } else if (methodName.startsWith("delete")) {
            methodName = methodName.substring("delete".length());
            return methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
        } else {
            // Verb must be all lowercase, lowercase only first letter of resource name

            String[] parts = methodName.split("(?=[A-Z])");

            String resource = "";

            for (int i = 0; i < parts.length; i++) {
                if (i == 1) {
                    resource += parts[i].toLowerCase();
                } else if (i > 1) {
                    resource += parts[i];
                }
            }

            return resource;
        }
    }

    public static String getCustomVerbFromPath(String pathInfo) {
        String[] pathParts = pathInfo.split("/");

        if (pathParts.length > 2) {
            return pathParts[pathParts.length - 1];
        } else {
            return "";
        }
    }

    public static String getCustomVerbFromMethod(String methodName) {
        if (methodName.startsWith("get")) {
            return "";
        } else if (methodName.startsWith("createOrReplace")) {
            return "";
        } else if (methodName.startsWith("create")) {
            return "";
        } else if (methodName.startsWith("update")) {
            return "";
        } else if (methodName.startsWith("delete")) {
            return "";
        } else {
            // Verb must be all lowercase

            String[] parts = methodName.split("(?=[A-Z])");

            String verb = "";

            for (int i = 0; i < parts.length; i++) {
                verb += parts[i].toLowerCase();
                break;
            }

            return verb;
        }
    }

    public static HttpMethod getHttpMethodFromServiceMethodName(String methodName) {
        if (methodName.startsWith("get")) {
            return HttpMethod.GET;
        } else if (methodName.startsWith("createOrReplace")) {
            return HttpMethod.PUT;
        } else if (methodName.startsWith("create")) {
            return HttpMethod.POST;
        } else if (methodName.startsWith("update")) {
            return HttpMethod.PATCH;
        } else if (methodName.startsWith("delete")) {
            return HttpMethod.DELETE;
        } else {
            return HttpMethod.POST;
        }
    }

    public Object handleInvocation(String httpMethod, String httpPath, Map<String, String[]> httpArguments) {
        String verb = "none";
        Object response;

        String resource = getResourceFromPath(httpPath);
        String customVerb = getCustomVerbFromPath(httpPath);

        try {
            if (!customVerb.isEmpty()) {
                verb = customVerb;
            } else if (httpMethod.compareTo(HttpMethod.GET.name()) == 0) {
                verb = "get";
            } else if (httpMethod.compareTo(HttpMethod.PUT.name()) == 0) {
                verb = "createOrReplace";
            } else if (httpMethod.compareTo(HttpMethod.POST.name()) == 0) {
                verb = "create";
            } else if (httpMethod.compareTo(HttpMethod.PATCH.name()) == 0) {
                verb = "update";
            } else if (httpMethod.compareTo(HttpMethod.DELETE.name()) == 0) {
                verb = "delete";
            } else {
                throw new IllegalArgumentException("The method " + httpMethod + " was not in the expected format.");
            }

            String ucFirstCharOfResourceName = resource.substring(0, 1).toUpperCase() + resource.substring(1);
            String methodName = verb + ucFirstCharOfResourceName;

            // Process HTTP arguments into map
            Map<String, Object> httpObjectList = getNestedMap(httpArguments);

            Object[] httpObjectNames = httpObjectList.keySet().toArray();

            // Format flat map into method signature for type list lookup
            String httpObjectNameListFormatted = "";

            for (int i = 0; i < httpObjectList.size(); i++) {
                httpObjectNameListFormatted += httpObjectNames[i].toString() + (i < (httpObjectList.size() - 1) ? ", " : "");
            }

            List<Class> methodParameterTypes = methodMap.get(methodName + '(' + httpObjectNameListFormatted + ')');

            Method method = this.webServiceClass.getMethod(methodName, methodParameterTypes.toArray(new Class[methodParameterTypes.size()]));

            // Now build up the actual argument list for use in invoking the real method, lining up types and values by parameter name
            Object[] methodArguments = new Object[methodParameterTypes.size()];
            for (int i = 0; i < methodParameterTypes.size(); i++) {
                Class methodParameterType = methodParameterTypes.get(i);

                Object methodArgument;
                if (customTypes.contains(methodParameterType.getName())) {
                    methodArgument = methodParameterType.getDeclaredConstructor().newInstance();

                    BeanInfo beanInfo = Introspector.getBeanInfo(methodParameterType);

                    PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

                    for (PropertyDescriptor descriptor : descriptors) {
                        Map<String, Object> httpObject = (Map<String, Object>) httpObjectList.get(httpObjectNames[i]);
                        BeanUtils.setProperty(methodArgument, descriptor.getName(), httpObject.get(descriptor.getName()));
                    }
                } else {
                    methodArgument = ConvertUtils.convert(httpObjectList.get(httpObjectNames[i]), methodParameterType);
                }

                methodArguments[i] = methodArgument;
            }

            response = method.invoke(this.webService, methodArguments);

            return response;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | IntrospectionException | RuntimeException e) {
            throw new IllegalArgumentException("Request invalid for action " + verb + " on resource " + resource + ". Check action, resource, and parameters for correctness.", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    private static Map<String, Object> getNestedMap(Map<String, String[]> dotKeyValues) {
        Map<String, Object> nestedMap = new TreeMap<>(); // Needs to be in predictable order
        Map<String, Object> parentNestedMap = nestedMap; // Remember starting position
        for (String dotKey : dotKeyValues.keySet()) {
            String[] dotKeys = dotKey.split("\\.");
            for (int i = 0; i < dotKeys.length; i++) {
                if (i < (dotKeys.length - 1)) {
                    // Not at the leaf, create holding map

                    Map<String, Object> node = new TreeMap<>(); // Needs to be in predictable order
                    Object previousNode = nestedMap.putIfAbsent(dotKeys[i], node);
                    if (previousNode != null) {
                        nestedMap = (Map<String, Object>) previousNode; // Set position to existing node
                    } else {
                        nestedMap = node; // Set position to new node
                    }
                } else {
                    // At the leaf, create the key-value

                    nestedMap.put(dotKeys[i], dotKeyValues.get(dotKey));
                    nestedMap = parentNestedMap; // Reset position to starting position
                }
            }
        }

        return nestedMap;
    }

    static String getDescription(Class annotationTarget) {
        String annotationValue = "";

        try {
            annotationValue = ((Description)annotationTarget.getDeclaredAnnotation(Description.class)).value();
        } catch (Exception e) {
            // Ignore, no annotation found
        }

        return annotationValue;
    }

    static String getDescription(Method annotationTarget) {
        String annotationValue = "";

        try {
            annotationValue = ((Description)annotationTarget.getDeclaredAnnotation(Description.class)).value();
        } catch (Exception e) {
            // Ignore, no annotation found
        }

        return annotationValue;
    }

    static String getName(Class annotationTarget) {
        String annotationValue = "";

        try {
            annotationValue = ((Name)annotationTarget.getDeclaredAnnotation(Name.class)).value();
        } catch (Exception e) {
            // Ignore, no annotation found
        }

        return annotationValue;
    }

    private String describeService() {
        this.webServiceMetadata.name = getName(this.webServiceClass);
        this.webServiceMetadata.description = getDescription(this.webServiceClass);
        this.webServiceMetadata.uri = this.uri;

        // Gather data, order and contextually correct sorting is important

        Map<String, Method> methodsSorted = new TreeMap<>();
        Map<Method, List<Class>> parameterClasses = new HashMap<>(); // new ArrayList();
        Map<Method, Set<String>> methodSignatureParameters = new HashMap<>(); //  = new HashSet<>();
        Method[] methods = webServiceClass.getDeclaredMethods();
        for (Method method : methods) {
            parameterClasses.putIfAbsent(method, new ArrayList<>());
            methodSignatureParameters.putIfAbsent(method, new TreeSet<>());

            String genericReturnType = method.getGenericReturnType().getTypeName();
            if (!genericReturnType.startsWith("java.lang") && !genericReturnType.contains("[]")) {
                customTypes.add(method.getGenericReturnType().getTypeName());
            }

            Parameter[] parameters = method.getParameters();
            methodsSorted.put(getResourceFromMethod(method.getName()) + method.getName() + Arrays.toString(parameters), method);
            for (Parameter parameter : parameters) {
                if (method.getName().startsWith("get") && parameters.length == 1) {
                    this.resourceKeyNames.putIfAbsent(getResourceFromMethod(method.getName()), parameter.getName());
                }

                String genericParameterType = parameter.getType().getTypeName();
                if (!genericParameterType.startsWith("java.lang") && !genericParameterType.contains("[]")) {
                    customTypes.add(parameter.getType().getTypeName());
                }

                parameterClasses.get(method).add(parameter.getType());
                methodSignatureParameters.get(method).add(parameter.getName());
            }
        }

        // Gather remaining data in proper sort order and build service data structure

        this.webServiceMetadata.methods = new LinkedHashSet<>();
        for (String methodKey : methodsSorted.keySet()) {
            WebService.ServiceMethod serviceMethod = new WebService.ServiceMethod();

            serviceMethod.returns = new WebService.ServiceMethod.MethodReturn();
            String genericReturnType = methodsSorted.get(methodKey).getGenericReturnType().getTypeName();
            serviceMethod.returns.type = simplifyName(genericReturnType);
            serviceMethod.name = methodsSorted.get(methodKey).getName();
            serviceMethod.description = getDescription(methodsSorted.get(methodKey));

            String customVerb = getCustomVerbFromMethod(serviceMethod.name);
            serviceMethod.uri = "/" + getResourceFromMethod(serviceMethod.name + (!customVerb.isEmpty() ? "/" + customVerb : ""));

            serviceMethod.parameters = new LinkedHashSet<>();
            Parameter[] parameters = methodsSorted.get(methodKey).getParameters();
            for (int i = 0; i < parameters.length; i++) {
                WebService.ServiceMethod.MethodParameter methodParameter = new WebService.ServiceMethod.MethodParameter();
                methodParameter.name = parameters[i].getName();
                methodParameter.type = simplifyName(parameters[i].getType().getCanonicalName());
                serviceMethod.parameters.add(methodParameter);
            }

            this.webServiceMetadata.methods.add(serviceMethod);

            StringBuilder methodSignature = new StringBuilder();
            methodSignature.append(serviceMethod.name + '(');
            Iterator<String> iterator = methodSignatureParameters.get(methodsSorted.get(methodKey)).iterator();
            for (int i = 0;  i < methodSignatureParameters.get(methodsSorted.get(methodKey)).size(); i++) {
                methodSignature.append(iterator.next() + (i < (methodSignatureParameters.get(methodsSorted.get(methodKey)).size() - 1) ? ", " : ""));
            }
            methodSignature.append(')');

            methodMap.put(methodSignature.toString(), parameterClasses.get(methodsSorted.get(methodKey)));
        }

        this.webServiceMetadata.customTypes = new LinkedHashSet<>();
        Iterator customTypesIterator = customTypes.iterator();
        while (customTypesIterator.hasNext()) {
            String type = (String) customTypesIterator.next();

            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(Class.forName(type.replace("[", "").replace("]", "")));

                PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors(); // Alphabetical order

                WebService.CustomType customType = new WebService.CustomType();
                customType.name = simplifyName(type);
                customType.description = (!beanInfo.getBeanDescriptor().getShortDescription().equals(customType.name) ? beanInfo.getBeanDescriptor().getShortDescription() : "");
                beanInfo.getBeanDescriptor().getShortDescription();
                customType.properties = new LinkedHashSet<>();

                for (PropertyDescriptor descriptor : descriptors) {
                    if (descriptor.getName().compareTo("class") != 0) {
                        WebService.CustomType.Property property = new WebService.CustomType.Property();
                        property.name = descriptor.getName();
                        property.description = (!descriptor.getShortDescription().equals(property.name) ? descriptor.getShortDescription() : "");
                        property.type = simplifyName(descriptor.getPropertyType().getTypeName());
                        customType.properties.add(property);
                    }
                }

                this.webServiceMetadata.customTypes.add(customType);
            } catch (ClassNotFoundException e) {
                // If class wasn't found, it's not a custom type, but something like void, int, a list. Not needed.
                customTypesIterator.remove();
            } catch (IntrospectionException e) {
                LOGGER.warning("Class " + type + " could not be fully introspected.");
            }
        }

        Set<WebService.Resource> resources = this.webServiceMetadata.resources = new LinkedHashSet<>();

        Map<String, Set<HttpMethod>> resourceAllowMethodsMap = new HashMap<>();

        for (WebService.ServiceMethod method : this.webServiceMetadata.methods) {
            WebService.Resource resource;

            if (resourceAllowMethodsMap.get(getResourceFromMethod(method.name)) == null) {
                resource = new WebService.Resource();
                resource.name = getResourceFromMethod(method.name);
                resource.uri = "/" + resource.name;

                Set<HttpMethod> allowMethods = new TreeSet<>();
                allowMethods.add(getHttpMethodFromServiceMethodName(method.name));
                resourceAllowMethodsMap.putIfAbsent(getResourceFromMethod(method.name), allowMethods);

                resource.allowMethods = allowMethods;

                resources.add(resource);
            } else {
                Set<HttpMethod> httpOptions = resourceAllowMethodsMap.get(getResourceFromMethod(method.name));
                httpOptions.add(getHttpMethodFromServiceMethodName(method.name));
            }
        }

        // Start outputting to description

        StringBuilder serviceDescription = new StringBuilder();

        serviceDescription.append(this.webServiceMetadata.name).append((!this.webServiceMetadata.name.isEmpty() && !this.webServiceMetadata.description.isEmpty() ? " - " : "")).append(this.webServiceMetadata.description).append(System.getProperty("line.separator"));

        serviceDescription.append(System.getProperty("line.separator"));

        for (WebService.ServiceMethod serviceMethod : this.webServiceMetadata.methods) {
            serviceDescription.append("Method: " + serviceMethod.name + (!serviceMethod.description.isEmpty() ? " - " + serviceMethod.description : "")).append(System.getProperty("line.separator"));

            for (WebService.ServiceMethod.MethodParameter methodParameter : serviceMethod.parameters) {
                serviceDescription.append("    Parameter: " + methodParameter.name + ", Type: " + methodParameter.type).append(System.getProperty("line.separator"));
            }

            serviceDescription.append("    Returns: " + serviceMethod.returns.type).append(System.getProperty("line.separator"));

            serviceDescription.append(System.getProperty("line.separator"));
        }

        for (WebService.CustomType customType : this.webServiceMetadata.customTypes) {
            serviceDescription.append("Type: " + customType.name + (!customType.description.isEmpty() ? " - " + customType.description : "")).append(System.getProperty("line.separator"));

            for (WebService.CustomType.Property property : customType.properties) {
                serviceDescription.append("    Property: " + property.name + ", Type: " + property.type + (!property.description.isEmpty() ? " - " + property.description : "")).append(System.getProperty("line.separator"));
            }

            serviceDescription.append(System.getProperty("line.separator"));
        }

        return serviceDescription.toString();
    }

    private static String simplifyName(String name) {
        String[] types = name.split("<");

        String newName = "";
        for (String type : types) {
            boolean hasJava = false;

            String[] nameSplit = type.split("\\.");

            if (nameSplit[0].compareTo("java") == 0) hasJava = true;

            name = nameSplit[nameSplit.length - 1];

            name = (hasJava ? name.toLowerCase() : name).replace(">", "");

            if (newName.length() > 0) {
                newName = newName + " of " + name;
            } else {
                newName = name;
            }
        }

        return newName;
    }
}
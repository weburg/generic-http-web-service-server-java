package com.weburg.ghowst;

import jdk.jfr.Description;
import jdk.jfr.Name;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;

import java.beans.*;
import java.lang.reflect.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.Logger;

public class HttpWebServiceMapper {
    private Object webService;
    private Class webServiceClass;
    private String webServiceUriBasePath;
    private WebService webServiceMetadata = new WebService();

    private Map<String, List<Class>> methodMap = new TreeMap<>();
    private Set<String> customTypes = new TreeSet<>();
    private String serviceDescription;
    private Map<String, String> resourceKeyNames = new HashMap<>();

    private static final Logger LOGGER = Logger.getLogger(HttpWebServiceMapper.class.getName());

    public HttpWebServiceMapper(Object webService, String webServiceUriBasePath) {
        this.webService = webService;
        this.webServiceClass = webService.getClass().getInterfaces()[0];
        this.webServiceUriBasePath = webServiceUriBasePath;
        this.serviceDescription = this.describeService(); // Populate before any invocations are made
    }

    public enum HttpMethod {
        GET, POST, PUT, PATCH, DELETE, OPTIONS
    }

    static class WebService {
        public String name;
        public String description;
        public String uriBasePath;
        public Set<ServiceMethod> serviceMethods;
        public Set<CustomType> customTypes;
        public Set<Resource> resources;

        static class ServiceMethod {
            public String name;
            public String description;
            public Set<MethodParameter> parameters;
            public MethodReturn returns;
            public String uriPath;
            public HttpMethod httpMethod;

            static class MethodParameter {
                public String name;
                public String type;
                public String description;
            }

            static class MethodReturn {
                public String type;
                public String description;
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
            public String uriPath;
            public Set<HttpMethod> allowMethods;
        }
    }

    public String getServiceDescription() {
        return this.serviceDescription;
    }

    public WebService getWebServiceMetadata() {
        return this.webServiceMetadata;
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

    public Object handleInvocation(String httpMethod, String httpPath, Map<String, Object[]> httpArguments) {
        String verb = "none";
        Object response;

        String resource = getResourceFromPath(httpPath);
        String customVerb = getCustomVerbFromPath(httpPath);

        try {
            if (httpMethod.compareTo(HttpMethod.GET.name()) == 0) {
                verb = "get";
            } else if (httpMethod.compareTo(HttpMethod.PUT.name()) == 0) {
                verb = "createOrReplace";
            } else if (httpMethod.compareTo(HttpMethod.POST.name()) == 0) {
                if (customVerb.isEmpty()) {
                    verb = "create";
                } else {
                    verb = customVerb;
                }
            } else if (httpMethod.compareTo(HttpMethod.PATCH.name()) == 0) {
                verb = "update";
            } else if (httpMethod.compareTo(HttpMethod.DELETE.name()) == 0) {
                verb = "delete";
            } else {
                throw new IllegalArgumentException("The method " + httpMethod + " was not in the expected format.");
            }

            if (!customVerb.isEmpty() && httpMethod.compareTo(HttpMethod.POST.name()) != 0) {
                throw new IllegalArgumentException("The method " + httpMethod + " cannot be used with a subresource (" + customVerb + ").");
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

            List<Class> methodParameterTypes;

            methodParameterTypes = methodMap.get(methodName + '(' + httpObjectNameListFormatted + ')');

            if (methodParameterTypes == null) {
                methodParameterTypes = methodMap.get(methodName);
            }

            if (methodParameterTypes == null) {
                throw new NoSuchMethodException();
            }

            Method method = this.webServiceClass.getMethod(methodName, methodParameterTypes.toArray(new Class[methodParameterTypes.size()]));

            Parameter[] methodParameters = method.getParameters();

            // Now build up the actual argument list for use in invoking the real method, lining up types and values by parameter name
            Object[] methodArguments = new Object[methodParameterTypes.size()];
            for (int i = 0; i < methodParameterTypes.size(); i++) {
                Parameter parameter = methodParameters[i];
                Class<?> methodParameterType = parameter.getType();
                Type methodGenericParameterType = parameter.getParameterizedType();
                Object httpValue = httpObjectList.get(parameter.getName());

                Object methodArgument;
                if (customTypes.contains(methodParameterType.getName())) {
                    methodArgument = methodParameterType.getDeclaredConstructor().newInstance();
                    BeanInfo beanInfo = Introspector.getBeanInfo(methodParameterType, methodParameterType.getSuperclass());

                    PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

                    for (PropertyDescriptor descriptor : descriptors) {
                        Map<String, Object> httpObject = (Map<String, Object>) httpValue;
                        BeanUtils.setProperty(methodArgument, descriptor.getName(), httpObject.get(descriptor.getName()));
                    }
                } else {
                    // The below helps read values when developing new supported datatypes
                    if (false) {
                        String a = methodParameterType.getName();
                        String b = methodParameterType.getSimpleName();
                        String c = methodParameterType.getCanonicalName();
                        String d = methodParameterType.getTypeName();
                        String e = methodGenericParameterType.getTypeName();
                        String f = "breakpoint this line";
                    }

                    if (methodParameterType == LocalDateTime.class) {
                        if (!((String[]) httpValue)[0].isEmpty()) {
                            try {
                                methodArgument = LocalDateTime.parse(((String[]) httpValue)[0], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                            } catch (DateTimeParseException e) {
                                throw new IllegalArgumentException("Invalid localdatetime format for parameter: " + parameter.getName() + ". Expected format is ISO-8601, seconds optional, up to 9 fractional digits optional, without timezone or offset, e.g. 2026-05-07T14:30 or 2026-05-07T14:30:00.000.");
                            }
                        } else {
                            methodArgument = null;
                        }
                    } else if (methodParameterType == OffsetDateTime.class) {
                        if (!((String[]) httpValue)[0].isEmpty()) {
                            try {
                                methodArgument = OffsetDateTime.parse(((String[]) httpValue)[0], DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                            } catch (DateTimeParseException e) {
                                throw new IllegalArgumentException("Invalid offsetdatetime format for parameter: " + parameter.getName() + ". Expected format is RFC3339, up to 9 fractional digits optional, with an offset or Zulu time, e.g. 2026-05-07T14:30:00-08:00, 2026-05-07T14:30:00.000Z.");
                            }
                        } else {
                            methodArgument = null;
                        }
                    } else if (List.class.isAssignableFrom(methodParameterType) && methodGenericParameterType instanceof ParameterizedType) {
                        if (httpValue != null) {
                            ParameterizedType parameterizedType = (ParameterizedType) methodGenericParameterType;
                            Type listItemType = parameterizedType.getActualTypeArguments()[0];

                            String[] values = (String[]) httpValue;
                            List<Object> convertedValues = new ArrayList<>();

                            for (String value : values) {
                                if (listItemType instanceof Class<?>) {
                                    convertedValues.add(ConvertUtils.convert(value, (Class<?>) listItemType));
                                } else {
                                    throw new IllegalArgumentException("Unsupported list item type: " + listItemType.getTypeName());
                                }
                            }

                            methodArgument = convertedValues;
                        } else {
                            methodArgument = new ArrayList<>();
                        }
                    } else if (Map.class.isAssignableFrom(methodParameterType) && methodGenericParameterType instanceof ParameterizedType) {
                        Map<String, String> values = new LinkedHashMap<>();

                        for (String parameterKey : httpObjectList.keySet()) {
                            if (parameterKey.startsWith(parameter.getName() + "[")) {
                                String mapKey = parameterKey.substring(parameter.getName().length() + 1, parameterKey.length() - 1);

                                if (mapKey.isEmpty()) {
                                    throw new IllegalArgumentException("Map keys cannot be empty for parameter: " + parameter.getName());
                                }

                                String mapValue = ((String[]) httpObjectList.get(parameterKey))[0];

                                values.put(mapKey, mapValue);
                            }
                        }

                        if (!values.isEmpty()) {
                            ParameterizedType parameterizedType = (ParameterizedType) methodGenericParameterType;
                            Type[] mapItemTypes = parameterizedType.getActualTypeArguments();

                            Map<String, Object> convertedValues = new LinkedHashMap<>();

                            for (String key : values.keySet()) {
                                int typeIndex = 0;
                                for (Type mapItemType : mapItemTypes) {
                                    if (typeIndex++ == 0) {
                                        continue; // Skip the first type as it's the key type; we rely on it already having been enforced as a String
                                    }

                                    if (mapItemType instanceof Class<?>) {
                                        convertedValues.put(key, ConvertUtils.convert(values.get(key), (Class<?>) mapItemType));
                                    } else {
                                        throw new IllegalArgumentException("Unsupported map item type: " + mapItemType.getTypeName());
                                    }
                                }
                            }

                            methodArgument = convertedValues;
                        } else {
                            methodArgument = new LinkedHashMap<>();
                        }
                    } else if (methodParameterType.isArray()) {
                        if (httpValue != null) {
                            methodArgument = ConvertUtils.convert((String[]) httpValue, methodParameterType.getComponentType());
                        } else {
                            methodArgument = Array.newInstance(methodParameterType.getComponentType(), 0);
                        }
                    } else if (methodParameterType == boolean.class || methodParameterType == Boolean.class) {
                        boolean booleanResult = false;
                        if (httpValue != null) {
                            String booleanValue = ((String[]) httpValue)[0];

                            if (booleanValue.compareToIgnoreCase("false") != 0 || booleanValue.compareToIgnoreCase("no") != 0 || booleanValue.compareToIgnoreCase("off") != 0 || booleanValue.compareToIgnoreCase("0") != 0) {
                                booleanResult = true;
                            }
                        }

                        methodArgument = ConvertUtils.convert(booleanResult, methodParameterType);
                    } else if (methodParameterType == java.io.File.class) {
                        if (httpValue == null) {
                            methodArgument = null;
                        } else {
                            if (httpValue.getClass().isArray() && httpValue.getClass().getComponentType() == java.io.File.class) {
                                methodArgument = ((java.io.File[]) httpValue)[0]; // It was preprocessed into a file already
                            } else {
                                throw new IllegalArgumentException("Expected a file for parameter: " + parameter.getName() + ". Check that the input type is file and that encoding type of the request is multipart/form-data.");
                            }
                        }
                    } else {
                        methodArgument = ConvertUtils.convert(((String[]) httpValue)[0], methodParameterType);
                    }
                }

                methodArguments[i] = methodArgument;
            }

            response = method.invoke(this.webService, methodArguments);

            return response;
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | IntrospectionException | RuntimeException e) {
            throw new IllegalArgumentException("Request invalid for action " + verb + " on resource " + resource + ". Check action, resource, any subresource, and parameters for correctness.", e);
        }
    }

    private static Map<String, Object> getNestedMap(Map<String, Object[]> dotKeyValues) {
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

    // Classes
    static String getDescription(Class annotationTarget) {
        String annotationValue = "";

        try {
            annotationValue = ((Description)annotationTarget.getDeclaredAnnotation(Description.class)).value();
        } catch (Exception e) {
            // Ignore, no annotation found
        }

        return annotationValue;
    }

    // Methods
    static String getDescription(Method annotationTarget) {
        String annotationValue = "";

        try {
            annotationValue = annotationTarget.getDeclaredAnnotation(Description.class).value();
        } catch (Exception e) {
            // Ignore, no annotation found
        }

        return annotationValue;
    }

    // Return
    static String getDescriptionInOut(AnnotatedType annotationTarget) {
        String annotationValue = "";

        try {
            annotationValue = annotationTarget.getDeclaredAnnotation(DescriptionInOut.class).value();
        } catch (Exception e) {
            // Ignore, no annotation found
        }

        return annotationValue;
    }

    // Parameter
    static String getDescriptionInOut(Parameter annotationTarget) {
        String annotationValue = "";

        try {
            annotationValue = annotationTarget.getDeclaredAnnotation(DescriptionInOut.class).value();
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
        this.webServiceMetadata.uriBasePath = this.webServiceUriBasePath;

        // Gather data, order and contextually correct sorting is important

        Map<String, Method> methodsSorted = new TreeMap<>();
        Map<Method, List<Class>> parameterClasses = new HashMap<>(); // new ArrayList();
        Map<Method, Set<String>> methodSignatureParameters = new HashMap<>(); //  = new HashSet<>();
        Method[] methods = webServiceClass.getDeclaredMethods();
        for (Method method : methods) {
            parameterClasses.putIfAbsent(method, new ArrayList<>());
            methodSignatureParameters.putIfAbsent(method, new TreeSet<>());

            String genericReturnType = method.getGenericReturnType().getTypeName();
            if (!genericReturnType.startsWith("java.") && !genericReturnType.contains("[]")) {
                customTypes.add(method.getGenericReturnType().getTypeName());
            }

            Parameter[] parameters = method.getParameters();
            methodsSorted.put(getResourceFromMethod(method.getName()) + method.getName() + Arrays.toString(parameters), method);
            for (Parameter parameter : parameters) {
                if (method.getName().startsWith("get") && parameters.length == 1) {
                    this.resourceKeyNames.putIfAbsent(getResourceFromMethod(method.getName()), parameter.getName());
                }

                String genericParameterType = parameter.getType().getTypeName();
                if (!genericParameterType.startsWith("java.") && !genericParameterType.contains("[]")) {
                    customTypes.add(parameter.getType().getTypeName());
                }

                parameterClasses.get(method).add(parameter.getType());
                methodSignatureParameters.get(method).add(parameter.getName());
            }
        }

        // Gather remaining data in proper sort order and build service data structure

        this.webServiceMetadata.serviceMethods = new LinkedHashSet<>();
        for (String methodKey : methodsSorted.keySet()) {
            WebService.ServiceMethod serviceMethod = new WebService.ServiceMethod();

            serviceMethod.name = methodsSorted.get(methodKey).getName();
            serviceMethod.description = getDescription(methodsSorted.get(methodKey));
            serviceMethod.returns = new WebService.ServiceMethod.MethodReturn();
            Type genericReturnType = methodsSorted.get(methodKey).getGenericReturnType();
            serviceMethod.returns.type = simplifyName(genericReturnType.getTypeName(), genericReturnType);
            serviceMethod.returns.description = getDescriptionInOut(methodsSorted.get(methodKey).getAnnotatedReturnType());

            String customVerb = getCustomVerbFromMethod(serviceMethod.name);
            serviceMethod.uriPath = "/" + getResourceFromMethod(serviceMethod.name + (!customVerb.isEmpty() ? "/" + customVerb : ""));
            serviceMethod.httpMethod = getHttpMethodFromServiceMethodName(serviceMethod.name);

            serviceMethod.parameters = new LinkedHashSet<>();
            Parameter[] parameters = methodsSorted.get(methodKey).getParameters();
            int optionalParameterCount = 0;
            for (int i = 0; i < parameters.length; i++) {
                WebService.ServiceMethod.MethodParameter methodParameter = new WebService.ServiceMethod.MethodParameter();
                methodParameter.name = parameters[i].getName();
                methodParameter.description = getDescriptionInOut(parameters[i]);
                methodParameter.type = simplifyName(parameters[i].getType().getCanonicalName(), parameters[i].getParameterizedType());
                serviceMethod.parameters.add(methodParameter);

                if (java.util.List.class.isAssignableFrom(parameters[i].getType()) || java.util.Map.class.isAssignableFrom(parameters[i].getType()) || parameters[i].getType().isArray() || parameters[i].getType() == boolean.class || parameters[i].getType() == Boolean.class || parameters[i].getType() == java.io.File.class) {
                    optionalParameterCount++;
                }
            }

            this.webServiceMetadata.serviceMethods.add(serviceMethod);

            StringBuilder methodSignature = new StringBuilder();
            methodSignature.append(serviceMethod.name + '(');
            Iterator<String> iterator = methodSignatureParameters.get(methodsSorted.get(methodKey)).iterator();
            for (int i = 0;  i < methodSignatureParameters.get(methodsSorted.get(methodKey)).size(); i++) {
                methodSignature.append(iterator.next() + (i < (methodSignatureParameters.get(methodsSorted.get(methodKey)).size() - 1) ? ", " : ""));
            }
            methodSignature.append(')');

            methodMap.put(methodSignature.toString(), parameterClasses.get(methodsSorted.get(methodKey)));

            if (optionalParameterCount > 0) {
                List<Class> previousMapping = methodMap.put(serviceMethod.name, parameterClasses.get(methodsSorted.get(methodKey)));

                if (previousMapping != null) {
                    throw new IllegalArgumentException("The same service method has multiple definitions containing optional parameters which is illegal: " + methodSignature.toString() +  ". Service methods can only have multiple definitions if all their parameters are required. Optional parameters are any parameter that might not be sent at all, e.g. arrays or booleans where the user did not select anything and the client omitted the field.");
                }
            }
        }

        this.webServiceMetadata.customTypes = new LinkedHashSet<>();
        Iterator customTypesIterator = customTypes.iterator();
        while (customTypesIterator.hasNext()) {
            String type = (String) customTypesIterator.next();

            try {
                Class<?> beanClass = Class.forName(type.replace("[", "").replace("]", ""));
                BeanInfo beanInfo = Introspector.getBeanInfo(beanClass, beanClass.getSuperclass());

                PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors(); // Alphabetical order

                WebService.CustomType customType = new WebService.CustomType();
                customType.name = simplifyName(type, null);
                customType.description = (!beanInfo.getBeanDescriptor().getShortDescription().equals(customType.name) ? beanInfo.getBeanDescriptor().getShortDescription() : "");
                beanInfo.getBeanDescriptor().getShortDescription();
                customType.properties = new LinkedHashSet<>();

                for (PropertyDescriptor descriptor : descriptors) {
                    if (descriptor.getName().compareTo("class") != 0) {
                        WebService.CustomType.Property property = new WebService.CustomType.Property();
                        property.name = descriptor.getName();
                        property.description = (!descriptor.getShortDescription().equals(property.name) ? descriptor.getShortDescription() : "");
                        property.type = simplifyName(descriptor.getPropertyType().getTypeName(), descriptor.getPropertyType());
                        customType.properties.add(property);
                    }
                }

                this.webServiceMetadata.customTypes.add(customType);
            } catch (ClassNotFoundException e) {
                // If class wasn't found, it's not a custom type, not needed.
                customTypesIterator.remove();
            } catch (IntrospectionException e) {
                LOGGER.warning("Class " + type + " could not be fully introspected.");
            }
        }

        Set<WebService.Resource> resources = this.webServiceMetadata.resources = new LinkedHashSet<>();

        Map<String, Set<HttpMethod>> resourceAllowMethodsMap = new HashMap<>();

        for (WebService.ServiceMethod method : this.webServiceMetadata.serviceMethods) {
            WebService.Resource resource;

            String fullyQualifiedResourceName = getResourceFromMethod(method.name);
            if (!getCustomVerbFromMethod(method.name).isEmpty()) {
                fullyQualifiedResourceName = fullyQualifiedResourceName + "." + getCustomVerbFromMethod(method.name);
            }

            if (resourceAllowMethodsMap.get(fullyQualifiedResourceName) == null) {
                resource = new WebService.Resource();
                resource.name = fullyQualifiedResourceName;
                resource.uriPath = "/" + getResourceFromMethod(method.name) + (!getCustomVerbFromMethod(method.name).isEmpty() ? '/' + getCustomVerbFromMethod(method.name) : "");

                Set<HttpMethod> allowMethods = new TreeSet<>();
                allowMethods.add(getHttpMethodFromServiceMethodName(method.name));
                resourceAllowMethodsMap.putIfAbsent(fullyQualifiedResourceName, allowMethods);

                resource.allowMethods = allowMethods;

                resources.add(resource);
            } else {
                Set<HttpMethod> httpOptions = resourceAllowMethodsMap.get(fullyQualifiedResourceName);
                httpOptions.add(getHttpMethodFromServiceMethodName(method.name));
            }
        }

        // Start outputting to description

        StringBuilder serviceDescription = new StringBuilder();

        serviceDescription.append(this.webServiceMetadata.name).append((!this.webServiceMetadata.name.isEmpty() && !this.webServiceMetadata.description.isEmpty() ? " - " : "")).append(this.webServiceMetadata.description).append(System.getProperty("line.separator"));

        serviceDescription.append(System.getProperty("line.separator"));

        for (WebService.ServiceMethod serviceMethod : this.webServiceMetadata.serviceMethods) {
            serviceDescription.append("Method: " + serviceMethod.name + (!serviceMethod.description.isEmpty() ? " - " + serviceMethod.description : "")).append(System.getProperty("line.separator"));

            for (WebService.ServiceMethod.MethodParameter methodParameter : serviceMethod.parameters) {
                serviceDescription.append("    Parameter: " + methodParameter.name + ", Type: " + methodParameter.type + (!methodParameter.description.isEmpty() ? " - " + methodParameter.description : "")).append(System.getProperty("line.separator"));
            }

            serviceDescription.append("    Returns: " + serviceMethod.returns.type + (!serviceMethod.returns.description.isEmpty() ? " - " + serviceMethod.returns.description : "")).append(System.getProperty("line.separator"));

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

    private static String simplifyName(String name, Type genericType) {
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type rawType = parameterizedType.getRawType();

            if (rawType instanceof Class<?> && List.class.isAssignableFrom((Class<?>) rawType)) {
                Type listItemType = parameterizedType.getActualTypeArguments()[0];

                return simplifyName(listItemType.getTypeName(), listItemType) + "[]";
            } else if (rawType instanceof Class<?> && Map.class.isAssignableFrom((Class<?>) rawType)) {
                Type mapKeyType = parameterizedType.getActualTypeArguments()[0];

                if (mapKeyType != String.class) {
                    throw new IllegalArgumentException("Maps in service methods must be strings, type was: " + mapKeyType.getTypeName());
                }

                Type mapValueType = parameterizedType.getActualTypeArguments()[1];

                return simplifyName(mapValueType.getTypeName(), mapValueType) + "{}";
            }

            name = normalizeTypeName(rawType);
        } else if (genericType != null) {
            name = normalizeTypeName(genericType);
        }

        String simpleName = name.substring(name.lastIndexOf('.') + 1).replace(">", "");

        return name.startsWith("java.") ? simpleName.toLowerCase() : simpleName;
    }

    private static String normalizeTypeName(Type type) {
        String name = type.getTypeName();

        if (type instanceof Class<?>) {
            if (type == Integer.class || type == int.class || type == Short.class || type == short.class || type == Long.class || type == long.class || type == Byte.class || type == byte.class) {
                name = "int";
            } else if (type == Float.class || type == float.class || type == Double.class || type == double.class) {
                name = "float";
            }
        }

        return name;
    }
}
package com.weburg.ghowst;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;

import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class HttpWebServiceMapper {
    private Object httpWebService;
    private Class webServiceClass;
    private Map<String, List<Class>> methodMap = new TreeMap<>();
    private Set<String> customTypes = new TreeSet<>();
    private String serviceDescription;
    private Map<String, String> resourceKeyNames = new HashMap<>();

    public HttpWebServiceMapper(Object httpWebService) {
        this.httpWebService = httpWebService;
        this.webServiceClass = httpWebService.getClass().getInterfaces()[0];
        this.serviceDescription = this.describeService(); // Populate before any invocations are made
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

    public static String getCustomVerb(String pathInfo) {
        String[] pathParts = pathInfo.split("/");

        if (pathParts.length > 2) {
            return pathParts[pathParts.length - 1];
        } else {
            return "";
        }
    }

    public Object handleInvocation(String httpMethod, String httpPath, Map<String, String[]> httpArguments) {
        String verb = "none";
        Object response;

        String resource = getResourceFromPath(httpPath);
        String customVerb = getCustomVerb(httpPath);

        try {
            if (!customVerb.isEmpty()) {
                verb = customVerb;
            } else if (httpMethod.compareTo("GET") == 0) {
                verb = "get";
            } else if (httpMethod.compareTo("PUT") == 0) {
                verb = "createOrReplace";
            } else if (httpMethod.compareTo("POST") == 0) {
                verb = "create";
            } else if (httpMethod.compareTo("PATCH") == 0) {
                verb = "update";
            } else if (httpMethod.compareTo("DELETE") == 0) {
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

            response = method.invoke(this.httpWebService, methodArguments);

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

    private String describeService() {
        // Gather data, order and contextually correct sorting is important

        Method[] methods = webServiceClass.getDeclaredMethods();

        Map<String, Method> methodsSorted = new TreeMap<>();
        Map<Method, List<Class>> parameterClasses = new HashMap<>(); // new ArrayList();
        Map<Method, Set<String>> methodSignatureParameters = new HashMap<>(); //  = new HashSet<>();
        for (Method method : methods) {
            parameterClasses.putIfAbsent(method, new ArrayList<>());
            methodSignatureParameters.putIfAbsent(method, new TreeSet<>());

            String genericReturnType = method.getGenericReturnType().getTypeName();
            if (!genericReturnType.startsWith("java.lang") && !genericReturnType.contains("[]")) {
                customTypes.add(method.getGenericReturnType().getTypeName());
            }

            Parameter[] parameters = method.getParameters();

            methodsSorted.put(getResourceFromMethod(method.getName()) + method.getName() + Arrays.toString(method.getParameters()), method);

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

        // Start outputting to description and gather remaining data in proper sort

        StringBuilder serviceDescription = new StringBuilder();

        for (String methodKey : methodsSorted.keySet()) {
            StringBuilder methodSignature = new StringBuilder();

            String genericReturnType = methodsSorted.get(methodKey).getGenericReturnType().getTypeName();
            serviceDescription.append("Method: " + methodsSorted.get(methodKey).getName() + ", Returns: " + simplifyName(genericReturnType)).append(System.getProperty("line.separator"));
            methodSignature.append(methodsSorted.get(methodKey).getName() + '(');

            Parameter[] parameters = methodsSorted.get(methodKey).getParameters();
            for (int i = 0; i < parameters.length; i++) {
                serviceDescription.append("    Parameter: " + parameters[i].getName() + ", Type: " + simplifyName(parameters[i].getType().getCanonicalName())).append(System.getProperty("line.separator"));
            }

            Iterator<String> iterator = methodSignatureParameters.get(methodsSorted.get(methodKey)).iterator();
            for (int i = 0;  i < methodSignatureParameters.get(methodsSorted.get(methodKey)).size(); i++) {
                methodSignature.append(iterator.next() + (i < (methodSignatureParameters.get(methodsSorted.get(methodKey)).size() - 1) ? ", " : ""));
            }

            serviceDescription.append(System.getProperty("line.separator"));
            methodSignature.append(')');

            methodMap.put(methodSignature.toString(), parameterClasses.get(methodsSorted.get(methodKey)));
        }

        Iterator customTypesIterator = customTypes.iterator();
        while (customTypesIterator.hasNext()) {
            String type = (String) customTypesIterator.next();

            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(Class.forName(type.replace("[", "").replace("]", "")));

                PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors(); // Alphabetical order

                serviceDescription.append("Type: " + simplifyName(type)).append(System.getProperty("line.separator"));

                for (PropertyDescriptor descriptor : descriptors) {
                    if (descriptor.getName().compareTo("class") != 0) {
                        serviceDescription.append("    Property: " + descriptor.getName() + ", Type: " + simplifyName(descriptor.getPropertyType().getTypeName())).append(System.getProperty("line.separator"));
                    }
                }

                serviceDescription.append(System.getProperty("line.separator"));
            } catch (ClassNotFoundException e) {
                // If class wasn't found, it's not a custom type, but something like void, int, a list. Not needed.
                customTypesIterator.remove();
            } catch (IntrospectionException e) {
                System.out.println("Class " + type + " could not be fully introspected.");
            }
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
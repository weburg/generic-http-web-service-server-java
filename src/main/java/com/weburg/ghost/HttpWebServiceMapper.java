package com.weburg.ghost;

import java.beans.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;

public class HttpWebServiceMapper {
    private Object httpWebService;
    private Class webServiceClass;
    private Map<String, List<Class>> methodMap = new HashMap<>();
    HashSet<Type> customTypes = new HashSet<>();
    private String serviceDescription;

    private class HttpServiceMethod {
        protected Class clazz;
        protected Map<String, String> properties;
    }

    public HttpWebServiceMapper(Object httpWebService) {
        this.httpWebService = httpWebService;
        this.webServiceClass = httpWebService.getClass().getInterfaces()[0];
        this.serviceDescription = this.describeService(); // Populate before any invocations are made
    }

    public String getServiceDescription() {
        return this.serviceDescription;
    }

    //public Object handleInvocation(Method method, Object[] arguments) {
    public Object handleInvocation(String httpMethod, String httpResource, Map<String, String[]> httpArguments) {
        String verb;
        Object response;

        try {
            if (httpMethod.compareTo("GET") == 0) {
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
                String[] parts = httpResource.split("(?=[A-Z])");

                if (parts.length != 2) {
                    throw new IllegalArgumentException("The resource " + httpResource + " was not in the expected format.");
                }

                verb = parts[1].toLowerCase();
            }

            String ucFirstCharOfResourceName = httpResource.substring(0, 1).toUpperCase() + httpResource.substring(1);
            String methodName = verb + ucFirstCharOfResourceName;

            // Process HTTP arguments into map
            Map<String, Object> httpObjectList = getNestedMap(httpArguments);

            // Format flat map into method signature for type list lookup
            String httpObjectNameListFormatted = "";
            int i = 0;
            for (String httpObjectName : httpObjectList.keySet()) {
                httpObjectNameListFormatted += httpObjectName + (i < (httpObjectList.size() - 1) ? ", " : "");
                i++;
            }

            List<Class> methodParameterTypes = methodMap.get(methodName + '(' + httpObjectNameListFormatted + ')');

            Method method = this.webServiceClass.getMethod(methodName, methodParameterTypes.get(0));

            // Now build up the actual argument list for use in invoking the real method, lining up types and values by parameter name
            int classCount = 0;
            for (Class methodParameterType : methodParameterTypes) {
                if (customTypes.contains(methodParameterType)) {
                    Object argument = methodParameterType.getDeclaredConstructor().newInstance();
                    //argument.getClass().getDeclaredFields();

                    BeanInfo beanInfo = Introspector.getBeanInfo(methodParameterType); // type.getTypeName().replace("[", "").replace("]", "")

                    PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

                    for (PropertyDescriptor descriptor : descriptors) {
                        //argument
                    }
                } else {
                    Object argument = methodParameterType.getConstructor(String.class).newInstance(httpObjectList.get("foo"));
                }

                ++classCount;
            }

            response = method.invoke(this.httpWebService, httpArguments);

            return response;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, Object> getNestedMap(Map<String, String[]> dotKeyValues) {
        Map<String, Object> nestedMap = new LinkedHashMap<>();
        Map<String, Object> parentNestedMap = nestedMap; // Remember starting position
        for (String dotKey : dotKeyValues.keySet()) {
            String[] dotKeys = dotKey.split("\\.");
            for (int i = 0; i < dotKeys.length; i++) {
                if (i < (dotKeys.length - 1)) {
                    // Not at the leaf, create holding map

                    Map<String, Object> node = new LinkedHashMap<>();
                    Object previousNode = nestedMap.putIfAbsent(dotKeys[i], node);
                    if (previousNode != null) {
                        nestedMap = (Map<String, Object>) previousNode; // Keep position at the same level
                    } else {
                        nestedMap = node; // Set position to the next level
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
        StringBuilder serviceDescription = new StringBuilder();

        Method[] methods = webServiceClass.getDeclaredMethods();

        for (Method method : methods) {
            StringBuilder methodSignature = new StringBuilder();

            String genericReturnType = method.getGenericReturnType().getTypeName();
            if (!genericReturnType.startsWith("java.lang") && !genericReturnType.contains("[]")) {
                customTypes.add(method.getGenericReturnType());
            }

            serviceDescription.append("Method: " + method.getName() + ", Returns: " + simplifyName(genericReturnType)).append(System.getProperty("line.separator"));
            methodSignature.append(method.getName() + '(');
            Parameter[] parameters = method.getParameters();

            int i = 0;
            List<Class> parameterClasses = new ArrayList<>();
            for (Parameter parameter : parameters) {
                String genericParameterType = parameter.getType().getTypeName();
                if (!genericParameterType.startsWith("java.lang") && !genericParameterType.contains("[]")) {
                    customTypes.add(parameter.getType());
                }

                serviceDescription.append("    Parameter: " + parameter.getName() + ", Type: " + simplifyName(parameter.getType().getCanonicalName())).append(System.getProperty("line.separator"));
                methodSignature.append(parameter.getName() + (i < (parameters.length - 1) ? ", " : ""));
                parameterClasses.add(parameter.getType());
                i++;
            }

            serviceDescription.append(System.getProperty("line.separator"));
            methodSignature.append(')');

            methodMap.put(methodSignature.toString(), parameterClasses);
        }

        Iterator customTypesIterator = customTypes.iterator();
        while (customTypesIterator.hasNext()) {
            Type type = (Type) customTypesIterator.next();

            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(Class.forName(type.getTypeName().replace("[", "").replace("]", "")));

                PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

                serviceDescription.append("Type: " + simplifyName(type.getTypeName())).append(System.getProperty("line.separator"));

                for (PropertyDescriptor descriptor : descriptors) {
                    if (descriptor.getName().compareTo("class") != 0) {
                        serviceDescription.append("    Property: " + descriptor.getName() + ", Type: " + simplifyName(descriptor.getPropertyType().getName())).append(System.getProperty("line.separator"));
                    }
                }

                serviceDescription.append(System.getProperty("line.separator"));
            } catch (ClassNotFoundException e) {
                // If class wasn't found, it's not a custom type, but something like void, int, a list. Not needed.
                customTypesIterator.remove();
            } catch (IntrospectionException e) {
                System.out.println("Class " + type.getTypeName() + " could not be fully introspected.");
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
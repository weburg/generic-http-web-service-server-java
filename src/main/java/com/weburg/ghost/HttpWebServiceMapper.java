package com.weburg.ghost;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.HashSet;

public class HttpWebServiceMapper {
    /*
    This is a very early implementation, most of the mapping is done manually
    in the servlet at this time.
     */

    /*
    public static Object newInstance(String baseUrl, Class webServiceClass) {
        Class[] interfaceArray = {webServiceClass};


        return java.lang.reflect.Proxy.newProxyInstance(
                webServiceClass.getClassLoader(),
                interfaceArray,
                new GenericHttpWebServiceClient(baseUrl));

    }
    */

    private Class webServiceClass;

    public HttpWebServiceMapper(Class webServiceClass) {
        this.webServiceClass = webServiceClass;
    }

    public String describeService() {
        StringBuilder serviceDescription = new StringBuilder();

        Method[] methods = webServiceClass.getDeclaredMethods();

        HashSet<Type> customTypeCandidates = new HashSet<>();

        for (Method method : methods) {
            String genericReturnType = method.getGenericReturnType().getTypeName();
            if (!genericReturnType.startsWith("java.lang") && !genericReturnType.contains("[]")) {
                customTypeCandidates.add(method.getGenericReturnType());
            }

            serviceDescription.append("Method: " + method.getName() + ", Returns: " + simplifyName(genericReturnType)).append(System.getProperty("line.separator"));
            Parameter[] parameters = method.getParameters();

            for (Parameter parameter : parameters) {
                String genericParameterType = parameter.getType().getTypeName();
                if (!genericParameterType.startsWith("java.lang") && !genericParameterType.contains("[]")) {
                    customTypeCandidates.add(parameter.getType());
                }

                serviceDescription.append("    Parameter: " + parameter.getName() + ", Type: " + simplifyName(parameter.getType().getCanonicalName())).append(System.getProperty("line.separator"));
            }

            serviceDescription.append(System.getProperty("line.separator"));
        }

        for (Type type : customTypeCandidates) {
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
            } catch (IntrospectionException e) {
                System.out.println("Class " + type.getTypeName() + " could not be fully introspected.");
            }
        }

        return serviceDescription.toString();
    }

    private String simplifyName(String name) {
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
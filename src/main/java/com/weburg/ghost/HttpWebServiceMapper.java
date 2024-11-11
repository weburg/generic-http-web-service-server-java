package com.weburg.ghost;

import com.weburg.services.HttpWebService;

import java.beans.*;
import java.lang.reflect.*;
import java.util.ArrayList;

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

        ArrayList<Type> customTypes = new ArrayList<>();

        for (Method method : methods) {
            String genericReturnType = method.getGenericReturnType().getTypeName();
            if (genericReturnType.contains(".") && !genericReturnType.startsWith("java")  && !genericReturnType.contains("[]")) {
                customTypes.add(method.getGenericReturnType());
            }

            serviceDescription.append("Method: " + method.getName() + ", Returns: " + simplifyName(genericReturnType)).append(System.getProperty("line.separator"));
            Parameter[] parameters = method.getParameters();

            for (Parameter parameter : parameters) {
                serviceDescription.append("    Parameter: " + parameter.getName() + ", Type: " + simplifyName(parameter.getType().getCanonicalName())).append(System.getProperty("line.separator"));
            }

            serviceDescription.append(System.getProperty("line.separator"));
        }

        for (Type type : customTypes) {
            serviceDescription.append("Type: " + simplifyName(type.getTypeName())).append(System.getProperty("line.separator"));

            BeanInfo beanInfo;

            try {
                beanInfo = Introspector.getBeanInfo(Class.forName(type.getTypeName().replace("[", "").replace("]", "")));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IntrospectionException e) {
                throw new RuntimeException(e);
            }

            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor descriptor : descriptors) {
                if (descriptor.getName().compareTo("class") != 0) {
                    serviceDescription.append("    Property: " + descriptor.getName() + ", Type: " + simplifyName(descriptor.getPropertyType().getName())).append(System.getProperty("line.separator"));
                }
            }

            serviceDescription.append(System.getProperty("line.separator"));
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

    public static void main(String[] args) {
        HttpWebServiceMapper wsm = new HttpWebServiceMapper(HttpWebService.class);

        System.out.println(wsm.describeService());
    }
}

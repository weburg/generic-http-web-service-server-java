import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8081);

        Context context = tomcat.addWebapp("", new File("src/main/webapp/").getAbsolutePath());
        context.addApplicationListener(ApplicationServletContextListener.class.getCanonicalName());
        context.addWelcomeFile("index");

        Connector connector = tomcat.getConnector();
        connector.setParseBodyMethods("POST,PUT,PATCH");

        tomcat.setConnector(connector);

        try {
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }

        tomcat.getServer().await();
    }
}

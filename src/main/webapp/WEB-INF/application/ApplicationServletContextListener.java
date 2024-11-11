import com.weburg.domain.Engine;
import com.weburg.services.DefaultHttpWebService;

import jakarta.servlet.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.logging.Logger;

public class ApplicationServletContextListener implements ServletContextListener {
    private static final Logger LOGGER = Logger.getLogger(ApplicationServletContextListener.class.getName());

    public void contextInitialized(ServletContextEvent event) {
        LOGGER.info("Context listener has kicked off!");

        event.getServletContext().addFilter("CorsFilter", new CorsFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

        Engine engine = new Engine();
        engine.setCylinders(14);

        event.getServletContext().addServlet("IndexServlet", new IndexServlet(engine)).addMapping("/index");

        String dataFilePath = System.getProperty("user.home") + System.getProperty("file.separator") + ".HttpWebService";

        File directory = new File(dataFilePath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        ArrayList<String> resourceFileNames = new ArrayList<>();
        resourceFileNames.add("arrow2.wav");
        resourceFileNames.add("arrow_x.wav");

        try {
            for (String resourceFileName : resourceFileNames) {
                Files.copy(this.getClass().getClassLoader().getResourceAsStream(resourceFileName), new File(dataFilePath + System.getProperty("file.separator") + resourceFileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DefaultHttpWebService httpWebService = new DefaultHttpWebService(dataFilePath);
        ServletRegistration.Dynamic genericHttpWebServiceServletRegistration = event.getServletContext()
                .addServlet("GenericHttpWebServiceServlet", new GenericHttpWebServiceServlet(httpWebService));
        genericHttpWebServiceServletRegistration.addMapping("/generichttpws/*", "/generichttpws");
        genericHttpWebServiceServletRegistration.setMultipartConfig(new MultipartConfigElement(dataFilePath));

        event.getServletContext().addServlet("SpaWebServiceServlet", new SpaWebServiceServlet()).addMapping("/spahttpws");

        event.getServletContext().addServlet("ViewCaptureServlet", new ViewCaptureServlet()).addMapping("/viewcapture");
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // No-op
    }
}

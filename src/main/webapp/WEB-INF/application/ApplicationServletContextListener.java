import example.domain.Engine;
import example.services.DefaultHttpWebService;
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

        event.getServletContext().addFilter("FormMemoryFilter", new FormMemoryFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/generichttpws/*");

        Engine engine = new Engine();
        engine.setName("Hemi");
        engine.setCylinders(12);

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
        String httpWebServiceUriPath = "/generichttpws";
        ServletRegistration.Dynamic exampleHttpWebServiceServletRegistration = event.getServletContext()
                .addServlet("ExampleHttpWebServiceServlet", new ExampleHttpWebServiceServlet(httpWebService, httpWebServiceUriPath));
        exampleHttpWebServiceServletRegistration.addMapping(httpWebServiceUriPath + "/*", httpWebServiceUriPath);
        exampleHttpWebServiceServletRegistration.setMultipartConfig(new MultipartConfigElement(dataFilePath));

        event.getServletContext().addServlet("htmlClient", new HtmlClientServlet()).addMapping("/htmlclient");
        
        event.getServletContext().addServlet("SpaWebServiceServlet", new SpaWebServiceServlet()).addMapping("/spahttpws");

        event.getServletContext().addServlet("ViewCaptureServlet", new ViewCaptureServlet()).addMapping("/viewcapture");
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // No-op
    }
}

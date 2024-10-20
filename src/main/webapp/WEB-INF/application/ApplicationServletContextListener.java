import com.weburg.domain.Engine;
import com.weburg.services.DefaultHttpWebService;

import jakarta.servlet.*;
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
        DefaultHttpWebService httpWebService = new DefaultHttpWebService(dataFilePath);
        ServletRegistration.Dynamic genericHttpWebServiceServletRegistration = event.getServletContext()
                .addServlet("GenericHttpWebServiceServlet", new GenericHttpWebServiceServlet(httpWebService));
        genericHttpWebServiceServletRegistration.addMapping("/generichttpws/*");
        genericHttpWebServiceServletRegistration.setMultipartConfig(new MultipartConfigElement(dataFilePath));

        event.getServletContext().addServlet("SpaWebServiceServlet", new SpaWebServiceServlet()).addMapping("/spahttpws");

        event.getServletContext().addServlet("ViewCaptureServlet", new ViewCaptureServlet()).addMapping("/viewcapture");
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // No-op
    }
}

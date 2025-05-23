import com.weburg.ghowst.HttpWebServiceServlet;
import example.domain.Image;
import example.domain.Sound;
import example.domain.Video;
import example.services.ExampleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.weburg.ghowst.HttpWebServiceMapper.getCustomVerbFromPath;
import static com.weburg.ghowst.HttpWebServiceMapper.getResourceFromPath;

public class ExampleHttpWebServiceServlet extends HttpWebServiceServlet {
    public ExampleHttpWebServiceServlet(ExampleService exampleService, String uriPath, String uploadTempPath) {
        super(exampleService, uriPath, uploadTempPath);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Object handledResponse = request.getAttribute("handledResponse");

        if (handledResponse != null) {
            if (getResourceFromPath(request.getPathInfo()).equals("sounds") && !getAccept(request).contains("text/html")) { // Accept: *.*
                File soundFileStored = ((Sound) handledResponse).getSoundFile();
                respondWithStream(response, soundFileStored);
            } else if (getResourceFromPath(request.getPathInfo()).equals("images") && getAccept(request).contains("image/") && !getAccept(request).contains("text/html")) {
                File imageFileStored = ((Image) handledResponse).getImageFile();
                respondWithStream(response, imageFileStored);
            } else if (getResourceFromPath(request.getPathInfo()).equals("videos") && !getAccept(request).contains("text/html")) {
                File videoFileStored = ((Video) handledResponse).getVideoFile();
                respondWithStream(response, videoFileStored);
            } else {
                String resource = getResourceFromPath(request.getPathInfo());
                respondWithResource(request, response, handledResponse, resource, resource);
            }
        } else {
            /*
            The root of the service, no work to do. If this was the root of the site, we could show a homepage here.
            However, we'll just redirect to the site's root for simplicity. The caller would have had to add the ?ahttpi
            query string to see the service description, if that's what they wanted.
             */

            response.setHeader("location", "/");
            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
        }
    }

    public void doNonGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Object handledResponse = request.getAttribute("handledResponse");

        String resourceKeyName = httpWebServiceMapper.getResourceKeyName(getResourceFromPath(request.getPathInfo()));

        if (handledResponse != null && resourceKeyName != null) {
            // Non-GET may have a subresource e.g. custom verb, so remove it to get URI to the parent resource

            String customVerb = getCustomVerbFromPath(request.getPathInfo());
            String requestUri;
            if (customVerb != "") {
                requestUri = request.getRequestURI().replace('/' + customVerb, "");
            } else {
                requestUri = request.getRequestURI();
            }

            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
            response.setHeader("location", requestUri + '?' + resourceKeyName + '=' + handledResponse);
        } else if (handledResponse != null) {
            if (getResourceFromPath(request.getPathInfo()).equals("trucks")) {
                if (getAccept(request).contains("text/html")) {
                    respondWithResource(request, response, handledResponse, getResourceFromPath(request.getPathInfo()), "result");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                PrintWriter write = response.getWriter();
                write.print(handledResponse);
                write.flush();
            }
        } else {
            response.setHeader("location", "/htmlclient");
            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
        }
    }

    protected void respondWithResource(HttpServletRequest request, HttpServletResponse response, Object handledResponse, String resource, String beanFieldName) throws ServletException, IOException {
        try {
            String resourceTitleCase = resource.substring(0, 1).toUpperCase() + resource.substring(1);
            Class beanClass = Class.forName("beans." + resourceTitleCase + "Bean");
            Object bean = beanClass.getConstructor().newInstance();

            try {
                Field field = bean.getClass().getDeclaredField(beanFieldName);
                if (field.getType() == List.class && !(handledResponse instanceof List)) {
                    handledResponse = Arrays.asList(handledResponse);
                }
            } catch (NoSuchFieldException e) {
                // Don't tamper with it
            }

            try {
                BeanUtils.setProperty(bean, beanFieldName, handledResponse);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
            request.setAttribute("model", bean);
            request.getRequestDispatcher("/WEB-INF/views/" + resource + ".jsp").forward(request, response);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected void doDescribe(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("serviceDescriptionText", httpWebServiceMapper.getServiceDescription());
        request.getRequestDispatcher("/WEB-INF/views/describe.jsp").forward(request, response);
    }
}
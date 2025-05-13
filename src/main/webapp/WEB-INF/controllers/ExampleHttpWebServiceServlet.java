import beans.EnginesBean;
import beans.PhotosBean;
import beans.SoundsBean;
import beans.TrucksBean;
import com.google.gson.Gson;
import com.weburg.ghowst.GenericHttpWebServiceServlet;
import example.domain.Engine;
import example.domain.Photo;
import example.domain.Sound;
import example.services.HttpWebService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static com.weburg.ghowst.HttpWebServiceMapper.getResourceFromPath;

public class ExampleHttpWebServiceServlet extends GenericHttpWebServiceServlet {
    public ExampleHttpWebServiceServlet(HttpWebService httpWebService) {
        super(httpWebService);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Object handledResponse = request.getAttribute("handledResponse");

        if (getAccept(request).contains("application/json")) {
            if (handledResponse != null) {
                response.setStatus(HttpServletResponse.SC_OK);

                Gson gson = new Gson();
                String idJson = gson.toJson(handledResponse);

                PrintWriter write = response.getWriter();
                write.print(idJson);
                write.flush();
            }
        } else {
            if (getResourceFromPath(request.getPathInfo()).equals("engines")) {
                if (!(handledResponse instanceof List)) {
                    handledResponse = Arrays.asList(handledResponse);
                }

                EnginesBean enginesBean = new EnginesBean();
                enginesBean.setEngines((List<Engine>) handledResponse);
                request.setAttribute("model", enginesBean);
                request.getRequestDispatcher("/WEB-INF/views/engines.jsp").forward(request, response);
            } else if (getResourceFromPath(request.getPathInfo()).equals("photos")) {
                if (getAccept(request).contains("text/html")) {
                    if (!(handledResponse instanceof List)) {
                        handledResponse = Arrays.asList(handledResponse);
                    }

                    PhotosBean photosBean = new PhotosBean();
                    photosBean.setPhotos((List<Photo>) handledResponse);
                    request.setAttribute("model", photosBean);
                    request.getRequestDispatcher("/WEB-INF/views/photos.jsp").forward(request, response);
                } else if (getAccept(request).contains("image/")) {
                    File photoFileStored = ((Photo) handledResponse).getPhotoFile();

                    respondWithStream(response, photoFileStored);
                }
            } else if (getResourceFromPath(request.getPathInfo()).equals("sounds")) {
                if (getAccept(request).contains("text/html")) {
                    if (!(handledResponse instanceof List)) {
                        handledResponse = Arrays.asList(handledResponse);
                    }

                    SoundsBean soundsBean = new SoundsBean();
                    soundsBean.setSounds((List<Sound>) handledResponse);
                    request.setAttribute("model", soundsBean);
                    request.getRequestDispatcher("/WEB-INF/views/sounds.jsp").forward(request, response);
                } else { // *.*
                    File soundFileStored = ((Sound) handledResponse).getSoundFile();

                    respondWithStream(response, soundFileStored);
                }
            }
        }
    }

    public void doNonGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Object handledResponse = request.getAttribute("handledResponse");

        if (getAccept(request).contains("application/json")) {
            // Programmatic responses

            response.setContentType("application/json");

            if (handledResponse != null) {
                // Creation generally just returns an identifier

                response.setStatus(HttpServletResponse.SC_CREATED);

                Gson gson = new Gson();
                String idJson = gson.toJson(handledResponse);

                PrintWriter write = response.getWriter();
                write.print(idJson);
                write.flush();
            } else {
                // Otherwise, void may be gotten (ephemeral)

                response.setStatus(HttpServletResponse.SC_OK);
            }
        } else {
            // Browser-oriented responses

            String resourceKeyName = httpWebServiceMapper.getResourceKeyName(getResourceFromPath(request.getPathInfo()));

            if (handledResponse != null && resourceKeyName != null) {
                response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                response.setHeader("location", request.getRequestURI() + '?' + resourceKeyName + '=' + handledResponse);
            } else if (handledResponse != null) {
                if (getResourceFromPath(request.getPathInfo()).equals("trucks")) {
                    if (getAccept(request).contains("text/html")) {
                        TrucksBean trucksBean = new TrucksBean();
                        trucksBean.setResult((String) handledResponse);
                        request.setAttribute("model", trucksBean);
                        request.getRequestDispatcher("/WEB-INF/views/trucks.jsp").forward(request, response);
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);
                    PrintWriter write = response.getWriter();
                    write.print(handledResponse);
                    write.flush();
                }
            } else {
                response.setHeader("location", "/generichttpwsclient.jsp");
                response.setStatus(HttpServletResponse.SC_SEE_OTHER);
            }
        }
    }
}
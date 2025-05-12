import beans.EnginesBean;
import beans.PhotosBean;
import beans.SoundsBean;
import com.google.gson.Gson;
import com.weburg.ghowst.HttpWebServiceMapper;
import com.weburg.ghowst.NotFoundException;
import example.domain.Engine;
import example.domain.Photo;
import example.domain.Sound;
import example.services.DefaultHttpWebService;
import example.services.HttpWebService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.weburg.ghowst.HttpWebServiceMapper.getResourceFromPath;

public class GenericHttpWebServiceServlet extends HttpServlet {
    private HttpWebService httpWebService;
    private HttpWebServiceMapper httpWebServiceMapper;

    private String dataFilePath;

    private static final Logger LOGGER = Logger.getLogger(GenericHttpWebServiceServlet.class.getName());

    public GenericHttpWebServiceServlet(HttpWebService httpWebService) {
        dataFilePath = ((DefaultHttpWebService) httpWebService).getDataFilePath();

        this.httpWebService = httpWebService;
        this.httpWebServiceMapper = new HttpWebServiceMapper(this.httpWebService);
    }

    private static String getAccept(HttpServletRequest request) {
        String acceptHeader = request.getHeader("accept");
        if (acceptHeader != null) {
            return request.getHeader("accept");
        } else {
            return "";
        }
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getMethod();

        // Reimplemented portions of original service
        if (method.equals("GET")) {
            long lastModified = getLastModified(request);
            if (lastModified == -1) {
                // servlet doesn't support if-modified-since, no reason
                // to go through further expensive logic
            } else {
                long ifModifiedSince;
                try {
                    ifModifiedSince = request.getDateHeader("If-Modified-Since");
                } catch (IllegalArgumentException iae) {
                    // Invalid date header - proceed as if none was set
                    ifModifiedSince = -1;
                }
                if (ifModifiedSince < (lastModified / 1000 * 1000)) {
                    // If the servlet mod time is later, call doGet()
                    // Round down to the nearest second for a proper compare
                    // A ifModifiedSince of -1 will always be less
                    if (!response.containsHeader("Last-Modified") && lastModified >= 0) {
                        response.setDateHeader("Last-Modified", lastModified);
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    return;
                }
            }

            response.setCharacterEncoding("UTF-8");

            doGet(request, response);
        } else if (request.getMethod().equals("OPTIONS") || request.getMethod().equals("POST") || request.getMethod().equals("PUT") || request.getMethod().equals("PATCH") || request.getMethod().equals("DELETE")) {
            LOGGER.info("Handling " + request.getMethod() + " at " + request.getPathInfo());

            Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());

            String contentType = request.getContentType();

            if (contentType != null && contentType.startsWith("multipart/form-data")) { // vs. application/x-www-form-urlencoded
                for (Part part : request.getParts()) {
                    String[] fileNames = {part.getSubmittedFileName()};

                    String[] priorFileNames = parameterMap.putIfAbsent(part.getName(), fileNames);
                    if (priorFileNames != null) {
                        String[] mergedFileNames = Arrays.copyOf(priorFileNames, priorFileNames.length + 1);
                        System.arraycopy(fileNames, 0, mergedFileNames, priorFileNames.length, 1);

                        parameterMap.put(part.getName(), mergedFileNames);
                    }
                }
            }

            try {
                Object handledResponse = httpWebServiceMapper.handleInvocation(request.getMethod(), request.getPathInfo(), parameterMap);

                if (contentType != null && contentType.startsWith("multipart/form-data")) {
                    for (Part part : request.getParts()) {
                        if (part.getSubmittedFileName() != null) {
                            part.write(part.getSubmittedFileName());
                        }
                    }
                }

                if (getAccept(request).contains("application/json")) {
                    response.setContentType("application/json");

                    if (handledResponse != null) {
                        response.setStatus(HttpServletResponse.SC_CREATED);

                        Gson gson = new Gson();
                        String idJson = gson.toJson(handledResponse);

                        PrintWriter write = response.getWriter();
                        write.print(idJson);
                        write.flush();
                    } else {
                        response.setStatus(HttpServletResponse.SC_OK);
                    }
                } else {
                    String resourceKeyName = httpWebServiceMapper.getResourceKeyName(getResourceFromPath(request.getPathInfo()));

                    if (handledResponse != null && resourceKeyName != null) {
                        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                        response.setHeader("location", request.getRequestURI() + '?' + resourceKeyName + '=' + handledResponse);
                    } else if (handledResponse != null) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        PrintWriter write = response.getWriter();
                        write.print(handledResponse);
                        write.flush();
                    } else {
                        response.setHeader("location", "/generichttpwsclient.jsp");
                        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                    }
                }
            } catch (RuntimeException e) {
                LOGGER.log(Level.SEVERE, "Failed", e);
                response.setHeader("access-control-expose-headers", "x-error-message");

                if (e instanceof NotFoundException || e.getCause() instanceof NotFoundException) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setHeader("x-error-message", (e instanceof NotFoundException ? e.getMessage() : e.getCause().getMessage()));
                } else if (e instanceof IllegalArgumentException || e.getCause() instanceof IllegalArgumentException) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setHeader("x-error-message", (e instanceof IllegalArgumentException ? e.getMessage() : e.getCause().getMessage()));
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.setHeader("x-error-message", "An internal server error occurred.");
                }
            }
        } else if (method.equals("OPTIONS")) {
            // allow vs. access-control-allow-methods
            doOptions(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("Handling GET at " + request.getPathInfo());

        if (getResourceFromPath(request.getPathInfo()).equals("engines")) {
            if (request.getParameter("id") != null) {
                try {
                    Engine engine = this.httpWebService.getEngines(new Integer(request.getParameter("id")));

                    if (!getAccept(request).contains("text/html")) {
                        response.setContentType("application/json");
                        Gson gson = new Gson();
                        String engineJson = gson.toJson(engine);

                        PrintWriter write = response.getWriter();
                        response.setStatus(HttpServletResponse.SC_OK);
                        write.print(engineJson);
                        write.flush();
                    } else {
                        EnginesBean enginesBean = new EnginesBean();
                        enginesBean.setEngines(Arrays.asList(engine));
                        request.setAttribute("model", enginesBean);
                        request.getRequestDispatcher("/WEB-INF/views/engines.jsp").forward(request, response);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed", e);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setHeader("access-control-expose-headers", "x-error-message");
                    response.setHeader("x-error-message", e.getMessage());
                }
            } else {
                try {
                    List<Engine> engines = this.httpWebService.getEngines();

                    if (getAccept(request).contains("application/json")) {
                        response.setContentType("application/json");
                        Gson gson = new Gson();
                        String json = gson.toJson(engines);

                        PrintWriter write = response.getWriter();
                        response.setStatus(HttpServletResponse.SC_OK);
                        write.print(json);
                        write.flush();
                    } else {
                        EnginesBean enginesBean = new EnginesBean();
                        enginesBean.setEngines(engines);
                        request.setAttribute("model", enginesBean);
                        request.getRequestDispatcher("/WEB-INF/views/engines.jsp").forward(request, response);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed", e);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setHeader("access-control-expose-headers", "x-error-message");
                    response.setHeader("x-error-message", e.getMessage());
                }
            }
        } else if (getResourceFromPath(request.getPathInfo()).equals("photos")) {
            if (request.getParameter("name") != null) {
                LOGGER.info("Photo service accept header: " + request.getHeader("accept"));

                File photoFileStored = new File(dataFilePath + System.getProperty("file.separator") + request.getParameter("name"));

                if (photoFileStored.exists()) {
                    try {
                        Photo photo = this.httpWebService.getPhotos(request.getParameter("name"));

                        if (getAccept(request).contains("application/json")) {
                            response.setContentType("application/json");
                            Gson gson = new Gson();
                            String json = gson.toJson(photo);

                            PrintWriter write = response.getWriter();
                            response.setStatus(HttpServletResponse.SC_OK);
                            write.print(json);
                            write.flush();
                        } else if (getAccept(request).contains("text/html")) {
                            PhotosBean photosBean = new PhotosBean();
                            photosBean.setPhotos(Arrays.asList(photo));
                            request.setAttribute("model", photosBean);
                            request.getRequestDispatcher("/WEB-INF/views/photos.jsp").forward(request, response);
                        } else if (getAccept(request).contains("image/")) {
                            response.setContentType(Files.probeContentType(photoFileStored.toPath()));
                            response.setContentLength((int) photoFileStored.length());

                            FileInputStream in = new FileInputStream(photoFileStored);
                            OutputStream out = response.getOutputStream();

                            // Copy the contents of the file to the output stream
                            byte[] buf = new byte[1024];
                            int count = 0;
                            while ((count = in.read(buf)) >= 0) {
                                out.write(buf, 0, count);
                            }
                            out.close();
                            in.close();
                        }
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Failed", e);
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.setHeader("access-control-expose-headers", "x-error-message");
                        response.setHeader("x-error-message", e.getMessage());
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {
                try {
                    List<Photo> photos = this.httpWebService.getPhotos();

                    if (getAccept(request).contains("application/json")) {
                        response.setContentType("application/json");
                        Gson gson = new Gson();
                        String json = gson.toJson(photos);

                        PrintWriter write = response.getWriter();
                        response.setStatus(HttpServletResponse.SC_OK);
                        write.print(json);
                        write.flush();
                    } else {
                        PhotosBean photosBean = new PhotosBean();
                        photosBean.setPhotos(photos);
                        request.setAttribute("model", photosBean);
                        request.getRequestDispatcher("/WEB-INF/views/photos.jsp").forward(request, response);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed", e);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setHeader("access-control-expose-headers", "x-error-message");
                    response.setHeader("x-error-message", e.getMessage());
                }
            }
        } else if (getResourceFromPath(request.getPathInfo()).equals("sounds")) {
            if (request.getParameter("name") != null) {
                File soundFileStored = new File(dataFilePath + System.getProperty("file.separator") + request.getParameter("name"));

                if (soundFileStored.exists()) {
                    try {
                        Sound sound = this.httpWebService.getSounds(request.getParameter("name"));

                        if (getAccept(request).contains("application/json")) {
                            response.setContentType("application/json");
                            Gson gson = new Gson();
                            String json = gson.toJson(sound);

                            PrintWriter write = response.getWriter();
                            response.setStatus(HttpServletResponse.SC_OK);
                            write.print(json);
                            write.flush();
                        } else if (getAccept(request).contains("text/html")) {
                            SoundsBean soundsBean = new SoundsBean();
                            soundsBean.setSounds(Arrays.asList(sound));
                            request.setAttribute("model", soundsBean);
                            request.getRequestDispatcher("/WEB-INF/views/sounds.jsp").forward(request, response);
                        } else { // *.*
                            String accept = getAccept(request);
                            accept.length();

                            response.setContentType(Files.probeContentType(soundFileStored.toPath()));
                            response.setContentLength((int) soundFileStored.length());

                            FileInputStream in = new FileInputStream(soundFileStored);
                            OutputStream out = response.getOutputStream();

                            // Copy the contents of the file to the output stream
                            byte[] buf = new byte[1024];
                            int count = 0;
                            while ((count = in.read(buf)) >= 0) {
                                out.write(buf, 0, count);
                            }
                            out.close();
                            in.close();
                        }
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Failed", e);
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.setHeader("access-control-expose-headers", "x-error-message");
                        response.setHeader("x-error-message", e.getMessage());
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setHeader("access-control-expose-headers", "x-error-message");
                    response.setHeader("x-error-message", "Resource not found");
                }
            } else {
                try {
                    List<Sound> sounds = this.httpWebService.getSounds();

                    if (getAccept(request).contains("application/json")) {
                        response.setContentType("application/json");
                        Gson gson = new Gson();
                        String json = gson.toJson(sounds);

                        response.setStatus(HttpServletResponse.SC_OK);
                        PrintWriter write = response.getWriter();
                        write.print(json);
                        write.flush();
                    } else {
                        SoundsBean soundsBean = new SoundsBean();
                        soundsBean.setSounds(sounds);
                        request.setAttribute("model", soundsBean);
                        request.getRequestDispatcher("/WEB-INF/views/sounds.jsp").forward(request, response);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed", e);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setHeader("access-control-expose-headers", "x-error-message");
                    response.setHeader("x-error-message", e.getMessage());
                }
            }
        } else {
            // Default to showing how to use the service
            /*OptionsBean optionsBean = new OptionsBean();
            optionsBean.setOptions(options);
            request.setAttribute("model", optionsBean);*/

            request.setAttribute("serviceDescriptionText", httpWebServiceMapper.getServiceDescription());
            request.getRequestDispatcher("/WEB-INF/views/describe.jsp").forward(request, response);
        }
    }
}
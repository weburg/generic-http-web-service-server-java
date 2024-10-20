import beans.EnginesBean;
import beans.PhotosBean;
import beans.SoundsBean;
import com.google.gson.Gson;
import com.weburg.domain.Engine;
import com.weburg.domain.Photo;
import com.weburg.domain.Sound;
import com.weburg.services.DefaultHttpWebService;
import com.weburg.services.HttpWebService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO this all needs to be handled generically via dynamic proxy around service class (or thereabouts)

/*
TODO Request and response should factor encoding type into the way processing is done on the way in and out
Right now, we assume form encoded data in and JSON data out.
*/

public class GenericHttpWebServiceServlet extends HttpServlet {
    private HttpWebService httpWebService;

    private String dataFilePath;

    private HashMap<String, Object> serviceLookup = new HashMap<>();

    private static final Logger LOGGER = Logger.getLogger(GenericHttpWebServiceServlet.class.getName());

    public GenericHttpWebServiceServlet(HttpWebService httpWebService) {
        dataFilePath = ((DefaultHttpWebService) httpWebService).getDataFilePath();

        this.httpWebService = httpWebService;

        serviceLookup.put("engines", this.httpWebService);
        serviceLookup.put("photos", this.httpWebService);
        serviceLookup.put("sounds", this.httpWebService);
        serviceLookup.put("keyboards", this.httpWebService);
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getMethod().equals("PATCH")) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpWebService service = getServiceFromPath(request.getPathInfo());
        response.setCharacterEncoding("UTF-8");

        if (getResource(request.getPathInfo()).equals("engines")) {
            if (request.getParameter("id") != null) {
                try {
                    Engine engine = service.getEngine(new Integer(request.getParameter("id")));

                    if (!getAccept(request).contains("text/html")) {
                        response.setContentType("application/json");
                        Gson gson = new Gson();
                        String engineJson = gson.toJson(engine);

                        PrintWriter write = response.getWriter();
                        response.setStatus(HttpServletResponse.SC_OK);
                        write.print(engineJson);
                    } else {
                        EnginesBean enginesBean = new EnginesBean();
                        enginesBean.setEngines(Arrays.asList(engine));
                        request.setAttribute("model", enginesBean);
                        request.getRequestDispatcher("/WEB-INF/views/engines.jsp").forward(request, response); // TODO This requires the preceeding slash, but if missing, there's no 404 reported
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed", e);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {
                try {
                    List<Engine> engines = service.getEngines();

                    if (!getAccept(request).contains("text/html")) {
                        response.setContentType("application/json");
                        Gson gson = new Gson();
                        String json = gson.toJson(engines);

                        PrintWriter write = response.getWriter();
                        response.setStatus(HttpServletResponse.SC_OK);
                        write.print(json);
                    } else {
                        EnginesBean enginesBean = new EnginesBean();
                        enginesBean.setEngines(engines);
                        request.setAttribute("model", enginesBean);
                        request.getRequestDispatcher("/WEB-INF/views/engines.jsp").forward(request, response);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed", e);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } else if (getResource(request.getPathInfo()).equals("photos")) {
            if (request.getParameter("photoFile") != null) {
                LOGGER.info("Photo service accept header: " + request.getHeader("accept"));

                File photoFileStored = new File(dataFilePath + System.getProperty("file.separator") + request.getParameter("photoFile"));

                if (photoFileStored.exists()) {
                    if (!getAccept(request).contains("text/html")) {
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
                    } else {
                        try {
                            Photo photo = service.getPhoto(request.getParameter("photoFile"));

                            PhotosBean photosBean = new PhotosBean();
                            photosBean.setPhotos(Arrays.asList(photo));
                            request.setAttribute("model", photosBean);
                            request.getRequestDispatcher("/WEB-INF/views/photos.jsp").forward(request, response);
                        } catch (Exception e) {
                            LOGGER.log(Level.SEVERE, "Failed", e);
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        }
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {
                try {
                    List<Photo> photos = service.getPhotos();

                    if (!getAccept(request).contains("text/html")) {
                        response.setContentType("application/json");
                        Gson gson = new Gson();
                        String json = gson.toJson(photos);

                        PrintWriter write = response.getWriter();
                        response.setStatus(HttpServletResponse.SC_OK);
                        write.print(json);
                    } else {
                        PhotosBean photosBean = new PhotosBean();
                        photosBean.setPhotos(photos);
                        request.setAttribute("model", photosBean);
                        request.getRequestDispatcher("/WEB-INF/views/photos.jsp").forward(request, response);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed", e);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } else if (getResource(request.getPathInfo()).equals("sounds")) {
            if (request.getParameter("soundFile") != null) {
                LOGGER.info("Sound service accept header: " + request.getHeader("accept"));

                File soundFileStored = new File(dataFilePath + System.getProperty("file.separator") + request.getParameter("soundFile"));

                if (soundFileStored.exists()) {
                    if (!getAccept(request).contains("text/html")) {
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
                    } else {
                        try {
                            Sound sound = service.getSound(request.getParameter("soundFile"));

                            SoundsBean soundsBean = new SoundsBean();
                            soundsBean.setSounds(Arrays.asList(sound));
                            request.setAttribute("model", soundsBean);
                            request.getRequestDispatcher("/WEB-INF/views/sounds.jsp").forward(request, response);
                        } catch (Exception e) {
                            LOGGER.log(Level.SEVERE, "Failed", e);
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        }
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {
                try {
                    List<Sound> sounds = service.getSounds();

                    if (!getAccept(request).contains("text/html")) {
                        response.setContentType("application/json");
                        Gson gson = new Gson();
                        String json = gson.toJson(sounds);

                        PrintWriter write = response.getWriter();
                        response.setStatus(HttpServletResponse.SC_OK);
                        write.print(json);
                    } else {
                        SoundsBean soundsBean = new SoundsBean();
                        soundsBean.setSounds(sounds);
                        request.setAttribute("model", soundsBean);
                        request.getRequestDispatcher("/WEB-INF/views/sounds.jsp").forward(request, response);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed", e);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        }
    }

    private static String getAccept(HttpServletRequest request) {
        String acceptHeader = request.getHeader("accept");
        if (acceptHeader != null) {
            return request.getHeader("accept");
        } else {
            return "";
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpWebService service = getServiceFromPath(request.getPathInfo());

        LOGGER.info("Handling POST at " + request.getPathInfo());
        String customVerb = getCustomVerb(request.getPathInfo());

        if (customVerb.isEmpty()) {
            if (getResource(request.getPathInfo()).equals("engines")) {
                Engine engine = new Engine();
                engine.setName(request.getParameter("name"));
                engine.setCylinders(new Integer(request.getParameter("cylinders")));
                engine.setThrottleSetting(new Integer(request.getParameter("throttleSetting")));

                try {
                    int id = service.createEngine(engine);

                    if (getAccept(request).contains("text/html")) {
                        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                    } else {
                        response.setStatus(HttpServletResponse.SC_CREATED);
                    }
                    response.setHeader("Location", "/generichttpws/engines?id=" + id);

                    Gson gson = new Gson();
                    String idJson = gson.toJson(id);

                    PrintWriter write = response.getWriter();
                    write.print(idJson);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed", e);
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                }
            } else if (getResource(request.getPathInfo()).equals("photos")) {
                Part photoPart = request.getPart("photoFile");

                Photo photo = new Photo();
                photo.setCaption(request.getParameter("caption"));
                photo.setPhotoFile(new File(photoPart.getSubmittedFileName()));

                LOGGER.info("File upload name: " + photoPart.getName());
                LOGGER.info("File upload submitted name: " + photoPart.getSubmittedFileName());
                LOGGER.info("File upload size: " + photoPart.getSize());

                try {
                    photoPart.write(photoPart.getSubmittedFileName());

                    String photoFileName = service.createPhoto(photo);

                    if (getAccept(request).contains("text/html")) {
                        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                    } else {
                        response.setStatus(HttpServletResponse.SC_CREATED);
                    }
                    response.setHeader("Location", "/generichttpws/photos?photoFile=" + photoFileName);

                    Gson gson = new Gson();
                    String idJson = gson.toJson(photoFileName);

                    PrintWriter write = response.getWriter();
                    write.print(idJson);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed", e);
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                }
            } else if (getResource(request.getPathInfo()).equals("sounds")) {
                Part soundPart = request.getPart("soundFile");

                Sound sound = new Sound();
                sound.setSoundFile(new File(soundPart.getSubmittedFileName()));

                LOGGER.info("File upload name: " + soundPart.getName());
                LOGGER.info("File upload submitted name: " + soundPart.getSubmittedFileName());
                LOGGER.info("File upload size: " + soundPart.getSize());

                try {
                    soundPart.write(soundPart.getSubmittedFileName());

                    String soundFileName = service.createSound(sound);

                    if (getAccept(request).contains("text/html")) {
                        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                    } else {
                        response.setStatus(HttpServletResponse.SC_CREATED);
                    }
                    response.setHeader("Location", "/generichttpws/sounds?soundFile=" + soundFileName);

                    Gson gson = new Gson();
                    String idJson = gson.toJson(soundFileName);

                    PrintWriter write = response.getWriter();
                    write.print(idJson);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed", e);
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
            }

        } else if (customVerb.equals("restart") || customVerb.equals("stop")) {
            if (getResource(request.getPathInfo()).equals("engines")) {
                // handle custom verb "restartEngines" at POST /engines/restart
                try {
                    if (customVerb.equals("restart")) {
                        service.restartEngine(new Integer(request.getParameter("id")));
                    } else if (customVerb.equals("stop")) {
                        service.stopEngine(new Integer(request.getParameter("id")));
                    }

                    if (getAccept(request).contains("text/html")) {
                        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                    } else {
                        response.setStatus(HttpServletResponse.SC_OK);
                    }
                    response.setHeader("Location", "/generichttpws/engines?id=" + request.getParameter("id"));
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
            }
        } else if (customVerb.equals("play")) {
            if (getResource(request.getPathInfo()).equals("sounds")) {
                service.playSound(request.getParameter("name"));
            }

            if (getAccept(request).contains("text/html")) {
                response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                response.setHeader("Location", "/generichttpwsclient.jsp");
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpWebService service = getServiceFromPath(request.getPathInfo());

        // TODO Generic HTTP WS Client needs to support PATCH, and servlet needs to handle file uploads in PUT, PATCH (only POST supports it now)

        Engine engine = new Engine();
        engine.setId(new Integer(request.getParameter("id")));
        engine.setName(request.getParameter("name"));
        engine.setCylinders(new Integer(request.getParameter("cylinders")));
        engine.setThrottleSetting(new Integer(request.getParameter("throttleSetting")));

        try {
            int id = service.createOrReplaceEngine(engine);

            if (getAccept(request).contains("text/html")) {
                response.setStatus(HttpServletResponse.SC_SEE_OTHER);
            } else {
                response.setStatus(HttpServletResponse.SC_CREATED);
            }
            response.setHeader("Location", "/generichttpws/engines?id=" + request.getParameter("id"));

            Gson gson = new Gson();
            String idJson = gson.toJson(id);

            PrintWriter write = response.getWriter();
            write.print(idJson);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed", e);
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }

    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("Handling PATCH at " + request.getPathInfo() + " with id " + request.getParameter("id"));

        HttpWebService service = getServiceFromPath(request.getPathInfo());

        // TODO Generic HTTP WS Client needs to support PATCH, and servlet needs to handle file uploads in PUT, PATCH (only POST supports it now)

        try {
            Engine engine = service.getEngine(new Integer(request.getParameter("id")));

            // Do the actual updating to the existing engine
            //engine.setId(new Integer(request.getParameter("id"))); // Do not change id
            if (request.getParameter("name") != null)
                engine.setName(request.getParameter("name"));
            if (request.getParameter("cylinders") != null)
                engine.setCylinders(new Integer(request.getParameter("cylinders")));
            if (request.getParameter("throttleSetting") != null)
                engine.setThrottleSetting(new Integer(request.getParameter("throttleSetting")));

            service.updateEngine(engine);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed", e);
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpWebService service = getServiceFromPath(request.getPathInfo());

        service.deleteEngine(new Integer(request.getParameter("id")));
    }

    private HttpWebService getServiceFromPath(String pathInfo) {
        String[] pathParts = pathInfo.split("/");

        HttpWebService service = (HttpWebService) serviceLookup.get(getResource(pathInfo));

        return service;
    }

    private String getResource(String pathInfo) {
        String[] pathParts = pathInfo.split("/");

        if (pathParts.length > 1) {
            return pathParts[1];
        } else {
            return "";
        }
    }

    private String getCustomVerb(String pathInfo) {
        String[] pathParts = pathInfo.split("/");

        if (pathParts.length > 2) {
            return pathParts[2];
        } else {
            return "";
        }
    }
}

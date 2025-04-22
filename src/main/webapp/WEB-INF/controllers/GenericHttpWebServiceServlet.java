import beans.EnginesBean;
import beans.PhotosBean;
import beans.SoundsBean;
import com.google.gson.Gson;
import example.domain.Engine;
import example.domain.Photo;
import example.domain.Sound;
import example.domain.Truck;
import com.weburg.ghost.HttpWebServiceMapper;

import example.services.DefaultHttpWebService;
import example.services.HttpWebService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenericHttpWebServiceServlet extends HttpServlet {
    private HttpWebService httpWebService;
    private HttpWebServiceMapper httpWebServiceMapper;

    private String dataFilePath;

    private static final Logger LOGGER = Logger.getLogger(GenericHttpWebServiceServlet.class.getName());

    public GenericHttpWebServiceServlet(HttpWebService httpWebService) {
        dataFilePath = ((DefaultHttpWebService) httpWebService).getDataFilePath();

        this.httpWebService = httpWebService;
        this.httpWebServiceMapper = new HttpWebServiceMapper(httpWebService.getClass().getInterfaces()[0]);
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getMethod().equals("PATCH")) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("Handling GET at " + request.getPathInfo());

        response.setCharacterEncoding("UTF-8");

        if (getResource(request.getPathInfo()).equals("engines")) {
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
                        request.getRequestDispatcher("/WEB-INF/views/engines.jsp").forward(request, response); // TODO This requires the preceeding slash, but if missing, there's no 404 reported
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
        } else if (getResource(request.getPathInfo()).equals("photos")) {
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
                        } else {
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
        } else if (getResource(request.getPathInfo()).equals("sounds")) {
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
                        } else {
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

            request.setAttribute("serviceDescriptionText", httpWebServiceMapper.describeService());
            request.getRequestDispatcher("/WEB-INF/views/describe.jsp").forward(request, response);
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
        LOGGER.info("Handling POST at " + request.getPathInfo());

        String customVerb = getCustomVerb(request.getPathInfo());

        if (customVerb.isEmpty()) {
            if (getResource(request.getPathInfo()).equals("engines")) {
                try {
                    Engine engine = new Engine();
                    engine.setName(request.getParameter("engine.name"));
                    engine.setCylinders(Integer.parseInt(request.getParameter("engine.cylinders")));
                    engine.setThrottleSetting(Integer.parseInt(request.getParameter("engine.throttleSetting")));

                    int id = this.httpWebService.createEngines(engine);

                    if (getAccept(request).contains("application/json")) {
                        response.setContentType("application/json");
                        Gson gson = new Gson();
                        String idJson = gson.toJson(id);

                        response.setStatus(HttpServletResponse.SC_CREATED);
                        PrintWriter write = response.getWriter();
                        write.print(idJson);
                        write.flush();
                    } else {
                        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                        response.setHeader("location", "/generichttpws/engines?id=" + id);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed", e);
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.setHeader("access-control-expose-headers", "x-error-message");
                    response.setHeader("x-error-message", e.getMessage());
                }
            } else if (getResource(request.getPathInfo()).equals("photos")) {
                Part photoPart = request.getPart("photo.photoFile");

                Photo photo = new Photo();
                photo.setCaption(request.getParameter("photo.caption"));
                photo.setPhotoFile(new File(photoPart.getSubmittedFileName()));

                LOGGER.info("File upload name: " + photoPart.getName());
                LOGGER.info("File upload submitted name: " + photoPart.getSubmittedFileName());
                LOGGER.info("File upload size: " + photoPart.getSize());

                try {
                    photoPart.write(photoPart.getSubmittedFileName());

                    String photoFileName = this.httpWebService.createPhotos(photo);

                    if (getAccept(request).contains("application/json")) {
                        response.setContentType("application/json");
                        Gson gson = new Gson();
                        String idJson = gson.toJson(photoFileName);

                        response.setStatus(HttpServletResponse.SC_CREATED);
                        PrintWriter write = response.getWriter();
                        write.print(idJson);
                        write.flush();
                    } else {
                        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                        response.setHeader("location", "/generichttpws/photos?name=" + photoFileName);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed", e);
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.setHeader("access-control-expose-headers", "x-error-message");
                    response.setHeader("x-error-message", e.getMessage());
                }
            } else if (getResource(request.getPathInfo()).equals("sounds")) {
                Part soundPart = request.getPart("sound.soundFile");

                Sound sound = new Sound();
                sound.setSoundFile(new File(soundPart.getSubmittedFileName()));

                LOGGER.info("File upload name: " + soundPart.getName());
                LOGGER.info("File upload submitted name: " + soundPart.getSubmittedFileName());
                LOGGER.info("File upload size: " + soundPart.getSize());

                try {
                    soundPart.write(soundPart.getSubmittedFileName());

                    String soundFileName = this.httpWebService.createSounds(sound);

                    if (getAccept(request).contains("application/json")) {
                        response.setContentType("application/json");
                        Gson gson = new Gson();
                        String idJson = gson.toJson(soundFileName);

                        response.setStatus(HttpServletResponse.SC_CREATED);
                        PrintWriter write = response.getWriter();
                        write.print(idJson);
                        write.flush();
                    } else {
                        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                        response.setHeader("location", "/generichttpws/sounds?name=" + soundFileName);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed", e);
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.setHeader("access-control-expose-headers", "x-error-message");
                    response.setHeader("x-error-message", e.getMessage());
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setHeader("access-control-expose-headers", "x-error-message");
                response.setHeader("x-error-message", "Verb not supported: " + customVerb);
            }

        } else if (customVerb.equals("restart") || customVerb.equals("stop")) {
            if (getResource(request.getPathInfo()).equals("engines")) {
                // handle custom verb "restartEngines" at POST /engines/restart
                try {
                    if (customVerb.equals("restart")) {
                        this.httpWebService.restartEngines(new Integer(request.getParameter("id")));
                    } else if (customVerb.equals("stop")) {
                        this.httpWebService.stopEngines(new Integer(request.getParameter("id")));
                    }

                    if (getAccept(request).contains("application/json")) {
                        response.setContentType("application/json");
                        response.setStatus(HttpServletResponse.SC_OK);
                    } else {
                        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                        response.setHeader("location", "/generichttpws/engines?id=" + request.getParameter("id"));
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed", e);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setHeader("access-control-expose-headers", "x-error-message");
                    response.setHeader("x-error-message", e.getMessage());
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setHeader("access-control-expose-headers", "x-error-message");
                response.setHeader("x-error-message", "Verb not supported: " + customVerb);
            }
        } else if (customVerb.equals("play")) {
            if (getResource(request.getPathInfo()).equals("sounds")) {
                try {
                    this.httpWebService.playSounds(request.getParameter("name"));

                    if (getAccept(request).contains("application/json")) {
                        response.setContentType("application/json");
                        response.setStatus(HttpServletResponse.SC_OK);
                    } else {
                        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                        response.setHeader("location", "/generichttpwsclient.jsp");
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed", e);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setHeader("access-control-expose-headers", "x-error-message");
                    response.setHeader("x-error-message", e.getMessage());
                }
            }
        } else if (customVerb.equals("race")) {
            if (getResource(request.getPathInfo()).equals("trucks")) {
                try {
                    Truck truck1 = new Truck();
                    truck1.setName(request.getParameter("truck1.name"));
                    truck1.setEngineId(Integer.parseInt(request.getParameter("truck1.engineId")));

                    Truck truck2 = new Truck();
                    truck2.setName(request.getParameter("truck2.name"));
                    truck2.setEngineId(Integer.parseInt(request.getParameter("truck2.engineId")));

                    int truckNameCompareResult = this.httpWebService.raceTrucks(truck1, truck2);

                    if (getAccept(request).contains("application/json")) {
                        response.setContentType("application/json");
                        Gson gson = new Gson();
                        String idJson = gson.toJson(truckNameCompareResult);

                        response.setStatus(HttpServletResponse.SC_OK);
                        PrintWriter write = response.getWriter();
                        write.print(idJson);
                        write.flush();
                    } else if (getAccept(request).contains("text/html")) {
                        response.setContentType("text/html");

                        response.setStatus(HttpServletResponse.SC_OK);
                        PrintWriter write = response.getWriter();
                        write.print("Lexicographical difference between truck names: " + truckNameCompareResult);
                        write.flush();
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed", e);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setHeader("access-control-expose-headers", "x-error-message");
                    response.setHeader("x-error-message", e.getMessage());
                }
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setHeader("access-control-expose-headers", "x-error-message");
            response.setHeader("x-error-message", "Verb not supported: " + customVerb);
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("Handling PUT at " + request.getPathInfo());

        // TODO Generic HTTP WS Client needs to support PATCH, and servlet needs to handle file uploads in PUT, PATCH (only POST supports it now)

        try {
            Engine engine = new Engine();
            engine.setId(Integer.parseInt(request.getParameter("engine.id")));
            engine.setName(request.getParameter("engine.name"));
            engine.setCylinders(Integer.parseInt(request.getParameter("engine.cylinders")));
            engine.setThrottleSetting(Integer.parseInt(request.getParameter("engine.throttleSetting")));

            int id = this.httpWebService.createOrReplaceEngines(engine);

            if (getAccept(request).contains("application/json")) {
                response.setContentType("application/json");
                Gson gson = new Gson();
                String idJson = gson.toJson(id);

                response.setStatus(HttpServletResponse.SC_CREATED);
                PrintWriter write = response.getWriter();
                write.print(idJson);
                write.flush();
            } else {
                response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                response.setHeader("location", "/generichttpws/engines?id=" + request.getParameter("engine.id"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed", e);
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.setHeader("access-control-expose-headers", "x-error-message");
            response.setHeader("x-error-message", e.getMessage());
        }
    }

    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("Handling PATCH at " + request.getPathInfo() + " with id " + request.getParameter("engine.id"));

        // TODO Generic HTTP WS Client needs to support PATCH, and servlet needs to handle file uploads in PUT, PATCH (only POST supports it now)

        try {
            Engine engine = this.httpWebService.getEngines(new Integer(request.getParameter("engine.id")));

            // Do the actual updating to the existing engine
            //engine.setId(new Integer(request.getParameter("engine.id"))); // Do not change id
            if (request.getParameter("engine.name") != null)
                engine.setName(request.getParameter("name"));
            if (request.getParameter("engine.cylinders") != null)
                engine.setCylinders(Integer.parseInt(request.getParameter("engine.cylinders")));
            if (request.getParameter("engine.throttleSetting") != null)
                engine.setThrottleSetting(Integer.parseInt(request.getParameter("engine.throttleSetting")));

            this.httpWebService.updateEngines(engine);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed", e);
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.setHeader("access-control-expose-headers", "x-error-message");
            response.setHeader("x-error-message", e.getMessage());
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("Handling DELETE at " + request.getPathInfo());

        this.httpWebService.deleteEngines(Integer.parseInt(request.getParameter("id")));
    }

    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.info("Handling OPTIONS at " + request.getPathInfo());

        // TODO describe the service via HttpWebServiceMapper, meanwhile see doGet
    }

    private String getResource(String pathInfo) {
        if (pathInfo == null) {
            return "";
        }

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
package com.weburg.ghowst;

import com.google.gson.Gson;
import com.weburg.ghowst.HttpWebServiceMapper.HttpMethod;
import example.services.HttpWebService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class GenericHttpWebServiceServlet extends HttpServlet {
    private final HttpWebService httpWebService;
    protected HttpWebServiceMapper httpWebServiceMapper;

    private static final Logger LOGGER = Logger.getLogger(GenericHttpWebServiceServlet.class.getName());

    public GenericHttpWebServiceServlet(HttpWebService httpWebService, String uri) {
        this.httpWebService = httpWebService;
        this.httpWebServiceMapper = new HttpWebServiceMapper(this.httpWebService, uri);
    }

    protected static String getAccept(HttpServletRequest request) {
        String acceptHeader = request.getHeader("accept");
        if (acceptHeader != null) {
            return request.getHeader("accept");
        } else {
            return "";
        }
    }

    protected final void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getMethod();

        if (method.equals(HttpMethod.OPTIONS.name())) {
            doOptions(request, response);
            return;
        }

        if (method.equals(HttpMethod.GET.name())) {
            // Pre-processing for GET requests
            // Reimplemented portions of original service

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

            if (request.getPathInfo() == null) {
                // At the root, default to showing usage
                doDescribe(request, response);
                return;
            }
        }

        LOGGER.fine("Handling " + method + " at " + request.getPathInfo());

        Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());

        String contentType = request.getContentType();

        if (contentType != null && contentType.startsWith("multipart/form-data")) { // vs. application/x-www-form-urlencoded
            for (Part part : request.getParts()) {
                if (part.getSubmittedFileName() != null) {
                    String[] fileNames = {part.getSubmittedFileName()};

                    String[] priorFileNames = parameterMap.putIfAbsent(part.getName(), fileNames);
                    if (priorFileNames != null) {
                        String[] mergedFileNames = Arrays.copyOf(priorFileNames, priorFileNames.length + 1);
                        System.arraycopy(fileNames, 0, mergedFileNames, priorFileNames.length, 1);

                        parameterMap.put(part.getName(), mergedFileNames);
                    }
                }
            }
        }

        try {
            Object handledResponse = httpWebServiceMapper.handleInvocation(request.getMethod(), request.getPathInfo(), parameterMap);
            request.setAttribute("handledResponse", handledResponse);

            if (contentType != null && contentType.startsWith("multipart/form-data")) {
                for (Part part : request.getParts()) {
                    if (part.getSubmittedFileName() != null) {
                        part.write(part.getSubmittedFileName());
                    }
                }
            }

            if (method.equals(HttpMethod.GET.name())) {
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
                    doGet(request, response);
                }
            } else {
                if (getAccept(request).contains("application/json")) {
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
                    doNonGet(request, response);
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
        } finally {
            if (contentType != null && contentType.startsWith("multipart/form-data")) {
                for (Part part : request.getParts()) {
                    if (part.getSubmittedFileName() != null) {
                        part.delete();
                    }
                }
            }
        }
    }

    protected abstract void doNonGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;

    protected abstract void doDescribe(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    protected static void respondWithStream(HttpServletResponse response, File file) {
        try {
            response.setContentType(Files.probeContentType(file.toPath()));
            response.setContentLength((int) file.length());

            FileInputStream in = new FileInputStream(file);
            OutputStream out = response.getOutputStream();

            // Copy the contents of the file to the output stream
            byte[] buf = new byte[1024];
            int count = 0;
            while ((count = in.read(buf)) >= 0) {
                out.write(buf, 0, count);
            }
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            throw new NotFoundException("The resource with name \"" + file.getName() + "\" was not found.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
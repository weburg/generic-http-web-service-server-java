package com.weburg.ghowst;

import com.google.gson.Gson;
import com.weburg.ghowst.HttpWebServiceMapper.HttpMethod;
import example.services.ExampleService;
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
import java.util.stream.Collectors;

public abstract class HttpWebServiceServlet extends HttpServlet {
    private final ExampleService exampleService;
    protected HttpWebServiceMapper httpWebServiceMapper;
    protected String uploadTempPath;

    private static final Logger LOGGER = Logger.getLogger(HttpWebServiceServlet.class.getName());

    public HttpWebServiceServlet(ExampleService exampleService, String uri, String uploadTempPath) {
        this.exampleService = exampleService;
        this.httpWebServiceMapper = new HttpWebServiceMapper(this.exampleService, uri);
        this.uploadTempPath = uploadTempPath;
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
                if (request.getParameter("ahttpi") != null) {
                    if (getAccept(request).contains("application/json")) {
                        response.setContentType("application/json");

                        response.setStatus(HttpServletResponse.SC_OK);

                        Gson gson = new Gson();
                        String webServiceMetadataJson = gson.toJson(httpWebServiceMapper.getWebServiceMetadata());

                        PrintWriter write = response.getWriter();
                        write.print(webServiceMetadataJson);
                        write.flush();
                    } else {
                        // Defer to child servlet for whatever formatting is supported
                        doDescribe(request, response);
                    }
                }

                // Nothing to do, delegate to the child servlet
                doGet(request, response);
                return;
            }
        }

        LOGGER.fine("Handling " + method + " at " + request.getPathInfo());

        Map<String, Object[]> parameterMap = new HashMap<>(request.getParameterMap());

        String contentType = request.getContentType();

        if (contentType != null && contentType.startsWith("multipart/form-data")) { // vs. application/x-www-form-urlencoded
            for (Part part : request.getParts()) {
                if (part.getSubmittedFileName() != null) {
                    File[] files = {new File(this.uploadTempPath + System.getProperty("file.separator") + part.getSubmittedFileName())};

                    File[] priorFiles = (File[]) parameterMap.putIfAbsent(part.getName(), files);
                    if (priorFiles != null) {
                        File[] mergedFiles = Arrays.copyOf(priorFiles, priorFiles.length + 1);
                        System.arraycopy(files, 0, mergedFiles, priorFiles.length, 1);

                        parameterMap.put(part.getName(), mergedFiles);
                    }
                }
            }
        }

        try {
            // Write the files eagerly so the service can read them directly
            if (contentType != null && contentType.startsWith("multipart/form-data")) {
                for (Part part : request.getParts()) {
                    if (part.getSubmittedFileName() != null) {
                        part.write(part.getSubmittedFileName());
                    }
                }
            }
        } catch (IOException e) {
            if (contentType != null && contentType.startsWith("multipart/form-data")) {
                for (Part part : request.getParts()) {
                    if (part.getSubmittedFileName() != null) {
                        part.delete();
                    }
                }
            }
        }

        try {
            Object handledResponse = httpWebServiceMapper.handleInvocation(request.getMethod(), request.getPathInfo(), parameterMap);
            request.setAttribute("handledResponse", handledResponse);

            if (method.equals(HttpMethod.GET.name())) {
                if (getAccept(request).contains("application/json")) {
                    response.setContentType("application/json");

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
                        try {
                            Files.deleteIfExists(new File(this.uploadTempPath + System.getProperty("file.separator") + part.getSubmittedFileName()).toPath());
                        } catch (IOException ex) {
                            LOGGER.log(Level.SEVERE, "Failed to delete temporary uploaded file: " + part.getSubmittedFileName(), ex);
                        }
                    }
                }
            }
        }
    }

    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpWebServiceMapper.WebService webServiceMetadata = this.httpWebServiceMapper.getWebServiceMetadata();

        for (HttpWebServiceMapper.WebService.Resource resource : webServiceMetadata.resources) {
            if (request.getPathInfo() != null && request.getPathInfo().equals(resource.uriPath)) {
                response.setHeader("access-control-allow-methods", resource.allowMethods.stream().map(Enum::name).collect(Collectors.joining(", ")));
                break;
            }
        }
    }

    protected abstract void doNonGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;

    protected abstract void doDescribe(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    protected static void respondWithStream(HttpServletResponse response, File file) {
        try (FileInputStream in = new FileInputStream(file); OutputStream out = response.getOutputStream()) {
            response.setContentType(Files.probeContentType(file.toPath()));
            response.setContentLength((int) file.length());

            // Copy the contents of the file to the output stream
            byte[] buf = new byte[1024];
            int count = 0;
            while ((count = in.read(buf)) >= 0) {
                out.write(buf, 0, count);
            }
        } catch (FileNotFoundException e) {
            throw new NotFoundException("The resource with name \"" + file.getName() + "\" was not found.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
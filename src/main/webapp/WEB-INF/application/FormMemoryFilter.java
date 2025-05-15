import jakarta.servlet.*;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FormMemoryFilter extends HttpFilter {
    public void init(FilterConfig filterConfig) throws ServletException {
        // No-op
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // Store form submit values in session for form re-population or other history tracking

        HttpSession session = request.getSession();
        Map<String, String[]> params = new HashMap<>(request.getParameterMap());

        Map<String, Map<String, String[]>> formData = (Map<String, Map<String, String[]>>)session.getAttribute("formData");
        if (formData == null) {
            session.setAttribute("formData", Map.of(request.getRequestURI(), params));
        } else {
            formData = new HashMap<>(formData);
            Map<String, String[]> existingParams = formData.get(request.getRequestURI());
            if (existingParams != null) {
                existingParams.putAll(params);
            } else {
                existingParams = params;
            }
            formData.put(request.getRequestURI(), existingParams);
            session.setAttribute("formData", formData);
        }

        filterChain.doFilter(request, response);
    }

    public void destroy() {
        // No-op
    }
}

import jakarta.servlet.*;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CorsFilter extends HttpFilter {
    public void init(FilterConfig filterConfig) throws ServletException {
        // No-op
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        //HttpServletRequest request = (HttpServletRequest) servletRequest;

        // Authorize (allow) domain(s) to consume the content
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods","GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD");

        /* None of the commented stuff is required, but is it not CORS compliant when OPTIONS is requested?
        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Methods","GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD");

        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        // For HTTP OPTIONS verb/method reply with ACCEPTED status code -- per CORS handshake
        if (request.getMethod().equals("OPTIONS")) {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            return;
        }
        */

        // Pass the request along the filter chain
        filterChain.doFilter(request, response);
    }

    public void destroy() {
        // No-op
    }
}

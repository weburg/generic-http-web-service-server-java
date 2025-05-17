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
        // Authorize (allow) domain(s) to consume the content
        response.addHeader("access-control-allow-origin", "*");

        // Pass the request along the filter chain
        filterChain.doFilter(request, response);
    }

    public void destroy() {
        // No-op
    }
}

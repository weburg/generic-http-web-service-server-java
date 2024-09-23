import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CorsFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {
        // No-op
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //HttpServletRequest request = (HttpServletRequest) servletRequest;

        // Authorize (allow) domain(s) to consume the content
        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Origin", "*");
        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Methods","GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD");

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
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
        // No-op
    }
}

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ViewCaptureServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(response) {
            private final StringWriter sw = new StringWriter();

            public PrintWriter getWriter() throws IOException {
                return new PrintWriter(this.sw);
            }

            public String toString() {
                return this.sw.toString();
            }
        };

        request.getRequestDispatcher("/WEB-INF/views/viewcapture.jsp").include(request, responseWrapper);
        String content = responseWrapper.toString().replaceFirst("peanut", "almond");
        response.getWriter().write(content);
    }
}

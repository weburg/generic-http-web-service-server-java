import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

class HtmlClientServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object formData = request.getSession(true).getAttribute("formData");

        if (formData != null) {
            request.setAttribute("form", formData);
        }
        request.getRequestDispatcher("/WEB-INF/views/htmlclient.jsp").forward(request, response);
    }
}
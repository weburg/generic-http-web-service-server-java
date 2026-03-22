import beans.AboutBean;
import beans.IndexBean;
import example.domain.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AboutServlet extends HttpServlet {
    public AboutServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AboutBean aboutBean = new AboutBean();
        aboutBean.setRequestUri(request.getRequestURI());

        request.setAttribute("model", aboutBean);
        request.getRequestDispatcher("/WEB-INF/views/about.jsp").forward(request, response);
    }
}
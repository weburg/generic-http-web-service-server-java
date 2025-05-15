import beans.IndexBean;

import example.domain.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IndexServlet extends HttpServlet {
    private Engine engine;

    public IndexServlet(Engine engine) {
        this.engine = engine;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        IndexBean indexBean = new IndexBean();
        indexBean.setEngine(engine);
        request.setAttribute("model", indexBean);

        request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
    }
}
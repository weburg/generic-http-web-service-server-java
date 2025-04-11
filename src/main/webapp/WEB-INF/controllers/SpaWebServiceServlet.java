import com.google.gson.Gson;

import example.domain.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class SpaWebServiceServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        PrintWriter write = response.getWriter();

        Engine engine = new Engine();
        engine.setThrottleSetting(17);

        Gson gson = new Gson();
        String engineJson = gson.toJson(engine);

        write.print(engineJson);
    }
}

import example.MyClass;
import viewbeans.About;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AboutServlet extends HttpServlet {
    public AboutServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        About about = new About();
        about.setRequestUri(request.getRequestURI());

        MyClass myClass = new MyClass();
        about.setMyFunctionOutput(myClass.myFunction("Java"));

        request.setAttribute("model", about);
        request.getRequestDispatcher("/WEB-INF/views/about.jsp").forward(request, response);
    }
}
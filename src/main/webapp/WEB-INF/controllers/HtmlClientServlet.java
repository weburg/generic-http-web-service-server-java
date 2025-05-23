import beans.HtmlClientBean;
import example.SupportedMimeTypes;
import example.services.ExampleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

class HtmlClientServlet extends HttpServlet {
    private ExampleService exampleService;

    public HtmlClientServlet(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object formData = request.getSession(true).getAttribute("formData");

        HtmlClientBean model = new HtmlClientBean();
        model.setFormData(formData);
        model.setSounds(this.exampleService.getSounds());
        model.setAudioTypesList(SupportedMimeTypes.getSubtypesAsCommaSeparatedString(SupportedMimeTypes.MimeTypes.AUDIO));
        model.setVideoTypesList(SupportedMimeTypes.getSubtypesAsCommaSeparatedString(SupportedMimeTypes.MimeTypes.VIDEO));
        model.setImageTypesList(SupportedMimeTypes.getSubtypesAsCommaSeparatedString(SupportedMimeTypes.MimeTypes.IMAGE));

        request.setAttribute("model", model);
        request.getRequestDispatcher("/WEB-INF/views/htmlclient.jsp").forward(request, response);
    }
}
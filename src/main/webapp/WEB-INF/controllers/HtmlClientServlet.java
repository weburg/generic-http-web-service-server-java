import beans.HtmlClientBean;
import example.SupportedMimeTypes;
import example.services.HttpWebService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

class HtmlClientServlet extends HttpServlet {
    private HttpWebService httpWebService;

    public HtmlClientServlet(HttpWebService httpWebService) {
        this.httpWebService = httpWebService;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object formData = request.getSession(true).getAttribute("formData");

        HtmlClientBean model = new HtmlClientBean();
        model.setFormData(formData);
        model.setSounds(this.httpWebService.getSounds());
        model.setAudioTypesList(SupportedMimeTypes.getSubtypesAsCommaSeparatedString(SupportedMimeTypes.MimeTypes.AUDIO));
        model.setVideoTypesList(SupportedMimeTypes.getSubtypesAsCommaSeparatedString(SupportedMimeTypes.MimeTypes.VIDEO));
        model.setImageTypesList(SupportedMimeTypes.getSubtypesAsCommaSeparatedString(SupportedMimeTypes.MimeTypes.IMAGE));

        request.setAttribute("model", model);
        request.getRequestDispatcher("/WEB-INF/views/htmlclient.jsp").forward(request, response);
    }
}
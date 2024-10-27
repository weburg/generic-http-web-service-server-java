<%@ page import="com.weburg.domain.Photo" %>
<jsp:useBean id="model" class="beans.PhotosBean" scope="request"/>
<jsp:include page="/WEB-INF/views/header.jsp">
    <jsp:param name="title" value="HTML Generic HTTP WS Client - Photos"/>
</jsp:include>

<table>
    <thead>
        <tr>
            <th>photo</th>
            <th>caption</th>
            <th>name</th>
        </tr>
    </thead>
    <tbody>
    <% for (Photo photo : model.getPhotos()) { %>
        <tr>
            <td><a href="${pageContext.request.contextPath}/generichttpws/photos?name=<%= photo.getName() %>"><img src="${pageContext.request.contextPath}/generichttpws/photos?name=<%= photo.getName() %>" width="128"></a></td>
            <td><%= photo.getCaption() %></td>
            <td><%= photo.getName() %></td>
        </tr>
    <% } %>
    </tbody>
</table>

<%@ include file="footer.jsp" %>
<%@ page import="com.weburg.domain.Sound" %>
<jsp:useBean id="model" class="beans.SoundsBean" scope="request"/>
<jsp:include page="/WEB-INF/views/header.jsp">
    <jsp:param name="title" value="HTML Generic HTTP WS Client - Sounds"/>
</jsp:include>

<table>
    <thead>
        <tr>
            <th>sound</th>
            <th>name</th>
        </tr>
    </thead>
    <tbody>
    <% for (Sound sound : model.getSounds()) { %>
        <tr>
            <td><audio controls><source src="${pageContext.request.contextPath}/generichttpws/sounds?name=<%= sound.getName() %>"></audio></td>
            <td><a href="${pageContext.request.contextPath}/generichttpws/sounds?name=<%= sound.getName() %>"><%= sound.getName() %></a></td>
        </tr>
    <% } %>
    </tbody>
</table>

<%@ include file="footer.jsp" %>
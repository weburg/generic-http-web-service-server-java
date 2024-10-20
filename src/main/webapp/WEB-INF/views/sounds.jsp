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
            <td><a href="${pageContext.request.contextPath}/generichttpws/sounds?soundFile=<%= sound.getSoundFile().getName() %>"><%= sound.getSoundFile().getName() %></a></td>
            <td><%= sound.getSoundFile().getName() %></td>
        </tr>
    <% } %>
    </tbody>
</table>

<%@ include file="footer.jsp" %>
<%@ page import="com.weburg.domain.Engine" %>
<jsp:useBean id="model" class="beans.EnginesBean" scope="request"/>
<jsp:include page="/WEB-INF/views/header.jsp">
    <jsp:param name="title" value="HTML Generic HTTP WS Client - Engines"/>
</jsp:include>

<table>
    <thead>
        <tr>
            <th>id</th>
            <th>name</th>
            <th>cylinders</th>
            <th>throttle</th>
        </tr>
    </thead>
    <tbody>
    <% for (Engine engine : model.getEngines()) { %>
        <tr>
            <td><a href="${pageContext.request.contextPath}/generichttpws/engines?id=<%= engine.getId() %>"><%= engine.getId() %></a></td>
            <td><%= engine.getName() %></td>
            <td><%= engine.getCylinders() %></td>
            <td><%= engine.getThrottleSetting() %></td>
        </tr>
    <% } %>
    </tbody>
</table>

<%@ include file="footer.jsp" %>
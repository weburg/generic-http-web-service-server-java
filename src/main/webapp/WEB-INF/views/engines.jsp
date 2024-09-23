<%@ page import="com.weburg.domain.Engine" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="model" class="beans.EnginesBean" scope="request"/>

<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>Engines</title>
	<link rel="stylesheet" href="/css/main.css">
</head>

<body>

<h1>HTML Plain HTTP WS Client - Engines</h1>

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
			<td><a href="${pageContext.request.contextPath}/plainhttpws/engines?id=<%= engine.getId() %>"><%= engine.getId() %></a></td>
			<td><%= engine.getName() %></td>
			<td><%= engine.getCylinders() %></td>
			<td><%= engine.getThrottleSetting() %></td>
		</tr>
	<% } %>
	</tbody>
</table>

<%@ include file="footer.jsp" %>

</body>
</html>
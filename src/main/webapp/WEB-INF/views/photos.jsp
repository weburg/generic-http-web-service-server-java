<%@ page import="com.weburg.domain.Photo" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="model" class="beans.PhotosBean" scope="request"/>

<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>Photos</title>
	<link rel="stylesheet" href="/css/main.css">
</head>

<body>

<h1>HTML Plain HTTP WS Client - Photos</h1>

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
			<td><a href="${pageContext.request.contextPath}/plainhttpws/photos?photoFile=<%= photo.getPhotoFile().getName() %>"><img src="${pageContext.request.contextPath}/plainhttpws/photos?photoFile=<%= photo.getPhotoFile().getName() %>" width="128"></a></td>
			<td><%= photo.getCaption() %></td>
			<td><%= photo.getPhotoFile().getName() %></td>
		</tr>
	<% } %>
	</tbody>
</table>

<%@ include file="footer.jsp" %>

</body>
</html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="model" class="beans.PhotosBean" scope="request"/>
<jsp:include page="header.jsp">
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
    <c:forEach var="photo" items="${model.photos}">
        <tr>
            <td><a href="${pageContext.request.contextPath}/generichttpws/photos?name=<c:out value="${photo.name}"/>"><img src="${pageContext.request.contextPath}/generichttpws/photos?name=<c:out value="${photo.name}"/>" width="128"></a></td>
            <td><c:out value="${photo.caption}"/></td>
            <td><c:out value="${photo.name}"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<%@ include file="footer.jsp" %>
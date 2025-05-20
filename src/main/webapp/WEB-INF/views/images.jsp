<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="model" class="beans.ImagesBean" scope="request"/>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="HTML Generic HTTP WS Client - Images"/>
</jsp:include>

<table>
    <thead>
        <tr>
            <th>image</th>
            <th>caption</th>
            <th>name</th>
        </tr>
    </thead>
    <tbody>
    <c:forEach var="image" items="${model.images}">
        <tr>
            <td><a href="${pageContext.request.contextPath}/generichttpws/images?name=<c:out value="${image.name}"/>"><img src="${pageContext.request.contextPath}/generichttpws/images?name=<c:out value="${image.name}"/>" width="128"></a></td>
            <td><c:out value="${image.caption}"/></td>
            <td><c:out value="${image.name}"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<%@ include file="footer.jsp" %>
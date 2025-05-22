<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="model" class="beans.SoundsBean" scope="request"/>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="HTML Generic HTTP WS Client - Sounds"/>
</jsp:include>

<table>
    <thead>
        <tr>
            <th>sound</th>
            <th>caption</th>
            <th>name</th>
        </tr>
    </thead>
    <tbody>
    <c:forEach var="sound" items="${model.sounds}">
        <tr>
            <td><audio controls><source src="${pageContext.request.contextPath}/generichttpws/sounds?name=<c:out value="${sound.name}"/>"></audio></td>
            <td><c:out value="${sound.caption}"/></td>
            <td><a href="${pageContext.request.contextPath}/generichttpws/sounds?name=<c:out value="${sound.name}"/>"><c:out value="${sound.name}"/></a></td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<%@ include file="footer.jsp" %>
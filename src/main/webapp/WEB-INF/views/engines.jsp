<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:useBean id="model" class="beans.EnginesBean" scope="request"/>
<jsp:include page="header.jsp">
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
    <c:forEach var="engine" items="${model.engines}">
        <tr>
            <td><a href="${pageContext.request.contextPath}/generichttpws/engines?id=<c:out value="${engine.id}"/>"><c:out value="${engine.id}"/></a></td>
            <td><c:out value="${engine.name}"/></td>
            <td><c:out value="${engine.cylinders}"/></td>
            <td><c:out value="${engine.throttleSetting}"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<%@ include file="footer.jsp" %>
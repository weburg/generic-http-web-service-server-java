<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="model" class="beans.ImagesBean" scope="request"/>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="HTML Generic HTTP WS Client - Images"/>
</jsp:include>

<c:choose>
    <c:when test="${param.get('name') == null}">
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
    </c:when>
    <c:otherwise>
        <c:set var="title" value="${model.images.get(0).caption != '' ? model.images.get(0).caption : model.images.get(0).name}"/>
        <p><c:out value="${title}"/></p>

        <div style="height: 75vh; width: 95vw;"><img src="${pageContext.request.contextPath}/generichttpws/images?name=<c:out value="${model.images.get(0).name}"/>" style="width: 100%; height: 100%; object-fit: contain; object-position: left;"></div>
    </c:otherwise>
</c:choose>

<%@ include file="footer.jsp" %>
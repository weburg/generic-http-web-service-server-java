<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="model" class="viewbeans.Videos" scope="request"/>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="HTML Generic HTTP WS Client - Videos"/>
</jsp:include>

<table>
    <thead>
        <tr>
            <th>video</th>
            <th>caption</th>
            <th>name</th>
        </tr>
    </thead>
    <tbody>
    <c:forEach var="video" items="${model.videos}">
        <tr>
            <td><video controls width="256"><source src="<c:out value="${pageContext.request.contextPath}"/>/generichttpws/videos?name=<c:out value="${video.name}"/>"></video></td>
            <td><c:out value="${video.caption}"/></td>
            <td><a href="<c:out value="${pageContext.request.contextPath}"/>/generichttpws/videos?name=<c:out value="${video.name}"/>"><c:out value="${video.name}"/></a></td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<%@ include file="footer.jsp" %>
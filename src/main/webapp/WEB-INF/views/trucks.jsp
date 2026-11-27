<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="model" class="viewbeans.Trucks" scope="request"/>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="HTML Generic HTTP WS Client - Trucks"/>
</jsp:include>

<h2>Well, race fans, get ready!</h2>

<p>
<c:if test="${not empty model.truckRaceResult}">
    <p>The <c:out value="${model.truckRaceResult.truck1.name}"/> and <c:out value="${model.truckRaceResult.truck2.name}"/> race!</p>

    <c:choose>
        <c:when test="${not empty model.truckRaceResult.winner}">
            The <c:out value="${model.truckRaceResult.winner.name}"/> wins!
        </c:when>
        <c:otherwise>
            The race is too close to call! It's a virtual tie.
        </c:otherwise>
    </c:choose>
</c:if>
</p>

<%@ include file="footer.jsp" %>
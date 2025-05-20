<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="model" class="beans.IndexBean" scope="request"/>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="Generic HTTP Web Service Server in Java (GHoWSt)"/>
</jsp:include>

<p>This server runs the generic HTTP web service backend as well as an HTML frontend to it as linked below, which has limited functionality because plain HTML cannot send all the HTTP methods. Generally, it can only do GET and POST. Use the JavaScript client or any other client for full functionality. That being said, actions on existing resources beyond simple create and read are supported and all use POST.</p>

<p>Probably, you'll be more interested in one of the links below than anything else on this page.</p>

<p>Test data for this project may involve a <c:out value="${model.engine.name}"/> with <c:out value="${model.engine.cylinders}"/> cylinders. Charge your batteries.

<%@ include file="footer.jsp" %>

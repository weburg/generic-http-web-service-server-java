<%@page import="example.domain.Engine" %>
<jsp:useBean id="model" class="beans.IndexBean" scope="request"/>
<jsp:include page="/WEB-INF/views/header.jsp">
    <jsp:param name="title" value="Home"/>
</jsp:include>

<p>This server runs the generic HTTP web service backend as well as an HTML frontend to it as linked below, which has limited functionality because plain HTML cannot send all the HTTP methods. Generally, it can only do GET and POST. Use the JavaScript client or any other client for full functionality. That being said, actions on existing resources beyond simple create and read are supported and all use POST.</p>

<p>Probably, you'll be more interested in one of the links below than anything else on this page.</p>

<p>But first, an example engine provided by a JSP bean!</p>
<ul>
    <li><%= model.getEngine().getName() %> w/ <%= model.getEngine().getCylinders() %> cylinders</li>
</ul>

<%@ include file="footer.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="model" class="viewbeans.Trucks" scope="request"/>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="HTML Generic HTTP WS Client - Trucks"/>
</jsp:include>

<h2>Well, race fans, here's the result of the race!</h2>

<p><pre><c:out value="${model.result}"/></pre></p>

<%@ include file="footer.jsp" %>
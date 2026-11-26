<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="model" class="viewbeans.Order" scope="request"/>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="HTML Generic HTTP WS Client - Order"/>
</jsp:include>

<h2>Your order</h2>

<p><c:out value="${model.result}" escapeXml="false"/></p>

<%@ include file="footer.jsp" %>
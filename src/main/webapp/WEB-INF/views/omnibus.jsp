<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="model" class="viewbeans.Omnibus" scope="request"/>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="HTML Generic HTTP WS Client - Omnibus"/>
</jsp:include>

<h2>Let's see all the fun stuff we can look at!</h2>

<p><pre><c:out value="${model.result}"/></pre></p>

<%@ include file="footer.jsp" %>
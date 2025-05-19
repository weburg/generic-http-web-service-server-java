<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="Describe the AHTTPI"/>
</jsp:include>

<pre><c:out value="${serviceDescriptionText}"/></pre>

<%@ include file="footer.jsp" %>
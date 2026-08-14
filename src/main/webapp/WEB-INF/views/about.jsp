<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:useBean id="model" class="viewbeans.About" scope="request"/>
<jsp:useBean id="myclass" class="example.MyClass" scope="request"/>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="About"/>
</jsp:include>

<h2>GHoWSt DUETS</h2>

<img src="/images/ghowstduetslogo.png" alt="GHoWSt DUETS logo" width="744" height="682" style="width: auto; height: 72px; float: left; margin-right: 10px; margin-bottom: 10px;">

<h3>Depend Upon Existing Technology Stack</h3>

<fmt:formatDate value="${model.date}" var="date" pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" timeZone="UTC" />
<p>The time is <c:out value="${date}"/>. <%= myclass.myFunction("Java") %>.</p>

<p>Request URI: <c:out value="${model.requestUri}"/></p>

<%@ include file="footer.jsp" %>
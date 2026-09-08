<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:useBean id="model" class="viewbeans.About" scope="request"/>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="About"/>
</jsp:include>

<h2>GHoWSt DEwETs</h2>

<img src="/images/ghowstdewetslogo.png" alt="GHoWSt DEwETs logo" width="744" height="682" style="width: auto; height: 72px; float: left; margin-right: 10px; margin-bottom: 10px;">

<h3>Dependable Elegant Existing Templates</h3>

<fmt:formatDate value="${model.date}" var="date" pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" timeZone="UTC" />
<p>The time is <c:out value="${date}"/>. <c:out value="${model.myFunctionOutput}"/>.</p>

<p>Request URI: <c:out value="${model.requestUri}"/></p>

<%@ include file="footer.jsp" %>
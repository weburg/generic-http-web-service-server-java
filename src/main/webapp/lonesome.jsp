<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="engine" class="com.weburg.domain.Engine" scope="request"/>
<html>
<head>
    <title>Standalone jsp</title>
    <link rel="stylesheet" href="/css/main.css">
</head>

<body>
    <p>All by myyyyself...!</p>

    <p>Engine</p>
    <ul>
        <li><%= engine.getName() %> w/ <%= engine.getCylinders() %> cylinders</li>
    </ul>

    <%@ include file="WEB-INF/views/footer.jsp" %>
</body>
</html>

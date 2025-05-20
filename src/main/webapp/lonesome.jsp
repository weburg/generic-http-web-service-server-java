<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="engine" class="example.domain.Engine" scope="request"/>
<html>
<head>
    <title>Standalone jsp</title>
    <link rel="stylesheet" href="/css/main.css">
</head>

<body>
    <p>All by myyyyself...!</p>

    <p>Engine</p>
    <ul>
        <li>${(not empty engine.name ? engine.name : "Unnamed")} w/ ${engine.cylinders} cylinders</li>
    </ul>

    <%@ include file="WEB-INF/views/footer.jsp" %>
</body>
</html>
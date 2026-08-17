<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${param.title}"/></title>
    <link rel="stylesheet" href="/styles/main.css">
</head>
<body>

<header>
    <h1><c:out value="${param.title}"/></h1>
</header>

<main>
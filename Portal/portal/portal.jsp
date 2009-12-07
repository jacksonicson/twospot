
<%@ page language="java" contentType="text/html; charset=UTF-8"
     pageEncoding="UTF-8" import="org.prot.portal.login.*"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<title>Portal</title>
</head>
<body>

<h1>Twospot portal</h1>

<h2>Navigation</h2>
<ul>
     <li><a href="logout.htm">Logout</a></li>
     <li><a href="registerApp.htm">Create a new App</a></li>
</ul>

<h2>Registered apps</h2>
<ul>
     <c:forEach var="appId" items="${appIds}">
          <li><a href="/app.htm?appId=${appId}">${appId}</a></li>
     </c:forEach>
</ul>

</body>
</html>
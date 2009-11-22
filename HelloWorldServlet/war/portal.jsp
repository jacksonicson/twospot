
<%@ page language="java" contentType="text/html; charset=UTF-8"
     pageEncoding="UTF-8" import="org.prot.portal.login.*"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<title>Portal</title>
</head>
<body>

Portal-Start page *
<a href="logout.htm">Logout</a>
*
<a href="registerApp.htm">Create a new App</a>

<br />

Applications
<ul>
     <c:forEach var="appId" items="${appIds}">
          <li><a href="/app.htm?id=${appId}">${appId}</a></li>
     </c:forEach>
</ul>

</body>
</html>
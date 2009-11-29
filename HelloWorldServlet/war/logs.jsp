
<%@ page language="java" contentType="text/html; charset=UTF-8"
     pageEncoding="UTF-8" import="org.prot.portal.login.*"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<title>Portal</title>
</head>
<body>

*
<a href="portal.htm">Portal</a>

<br />

Logs:
<ul>
     <c:forEach var="logMessage" items="${logMessages}">
          <p>${logMessage}</p>
     </c:forEach>
</ul>

</body>
</html>
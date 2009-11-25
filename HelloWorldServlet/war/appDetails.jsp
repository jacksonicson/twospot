
<%@ page language="java" contentType="text/html; charset=UTF-8"
     pageEncoding="UTF-8" import="org.prot.portal.login.*"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<title>Portal</title>
</head>
<body>

<ul>
     <li><a href="/portal.htm">Portal</a></li>
</ul>

<p>Details for the Application: ${appId}</p>

<p>
<ul>
     <li>Logfiles</li>
     <li><a href="/dbBrowser.htm?appId=${appId}">Database browser</a></li>
     <li>Versions</li>
</ul>
</p>

</body>
</html>
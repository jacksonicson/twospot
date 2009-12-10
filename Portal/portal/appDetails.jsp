
<%@ page language="java" contentType="text/html; charset=UTF-8"
     pageEncoding="UTF-8" import="org.prot.portal.login.*"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<title>TwoSpot - Portal</title>
<link href="./etc/twospot.css" type="text/css" rel="stylesheet" />
</head>
<body>

<div id="header">
<h1>TwoSpot - Portal</h1>
</div>

<div id="page">
<p><a href="/portal.htm">Back</a></p>


<h2>Details of AppId: ${appId}</h2>

<p>
<ul>
     <li><a href="/logs.htm?appId=${appId}">Logfiles</a></li>
     <li><a href="/dbBrowser.htm?appId=${appId}">Datastore browser</a></li>
</ul>
</p>

</div>

</body>
</html>
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
    <h2>Options</h2>
    <ul>
        <li><a href="logout.htm">Logout</a></li>
        <li><a href="registerApp.htm">Register a new AppId</a></li>
    </ul>
    <h2>Your AppId's</h2>
    <table border="0" bordercolor="#FFFFFF">
        <c:forEach var="appId" items="${appIds}">
            <tr>
                <td><p><a href="/app.htm?appId=${appId}">${appId}</a></p></td>
                <td></td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>
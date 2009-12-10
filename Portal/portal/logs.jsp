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
    <p><a href="portal.htm">Portal</a></p>
    <h2>Log messages:</h2>
    <table>
        <c:forEach var="logMessage" items="${logMessages}">
            <tr class="log_table">
				<td class="log_table_err_${logMessage.severity}"></td>
                <td class="log_table_msg">${logMessage.message}</td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>
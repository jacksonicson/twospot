
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
    <h2>Database browser: ${appId}</h2>
    
    <form:form action="/dbQueryForm.htm" commandName="queryCommand">
	
     <form:hidden path="appId" />
     <table>
          <tr>
               <td>Table:</td>
               <td><form:select path="table">
                    <form:options items="${tableList}" />
               </form:select></td>
          </tr>
          <tr>
               <td>Query: <form:errors path="query" /></td>
               <td><form:input path="query" /></td>
          </tr>
          <tr>
               <td colspan="2"><input type="submit" value="Run" /></td>
          </tr>
     </table>
</form:form>

<br/>

<table border="1">
     <tr>
          <c:forEach var="head" items="${dataTableHead}">
               <td>${head}</td>
          </c:forEach>
     </tr>

     <c:forEach var="tablet" items="${dataTablet}">
          <tr>
               <c:forEach var="cellData" items="${tablet}">
                    <td>${cellData}</td>
               </c:forEach>
          </tr>
     </c:forEach>
</table>

</div>

</body>
</html>
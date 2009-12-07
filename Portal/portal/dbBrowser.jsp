
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

<p>Database browser: ${appId}</p>


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

</body>
</html>
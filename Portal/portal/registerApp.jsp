
<%@ page language="java" contentType="text/html; charset=UTF-8"
     pageEncoding="UTF-8" import="org.prot.portal.login.*"%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<title>Portal</title>
</head>
<body>

<p>Navigation</p>
<ul>
     <li><a href="/portal.htm">Portal</a></li>
     <li><a href="/logout.htm">Logout</a></li>
</ul>

<p>Create a new Application</p>
<form:form action="/registerApp.htm" method="POST"
     commandName="registerAppCommand">
     <table>
          <tr>
               <td>AppId<form:errors path="appId" /></td>
               <td><form:input path="appId" /></td>
          </tr>
          <tr>
               <td colspan="2"><input type="submit" value="Ok" /></td>
          </tr>
     </table>
</form:form>


</body>
</html>
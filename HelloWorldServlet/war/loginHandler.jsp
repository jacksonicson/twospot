
<%@ page language="java" contentType="text/html; charset=UTF-8"
     pageEncoding="UTF-8" import="org.prot.portal.login.*"
%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<title>Portal</title>
</head>
<body>

<p>Login:</p>

<form:form action="loginHandler.htm" method="POST"
     commandName="loginCommand"
>
     <form:hidden path="redirectUrl" />

     <p><form:errors path="username" /> <form:errors
          path="password"
     /></p>

     <table>
          <tr>
               <td>Username:</td>
               <td><form:input path="username" size="60" /></td>
          </tr>
          <tr>
               <td>Password:</td>
               <td><form:input path="password" size="60" /></td>
          </tr>
          <tr>
               <td colspan="2"><input type="submit" value="Post" /></td>
          </tr>
     </table>

</form:form>


</body>
</html>
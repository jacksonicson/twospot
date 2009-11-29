<%@ page language="java" contentType="text/html; charset=UTF-8"
     pageEncoding="UTF-8" import="org.prot.portal.login.*"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<title>Portal</title>
</head>
<body>

<p>Register:</p>

<form:form action="registerHandler.htm" method="POST" commandName="registerCommand">

 <table>
          <tr>
               <td>Username: <form:errors path="username" /></td>
               <td><form:input path="username" size="15" /></td>
          </tr>
          <tr>
               <td>Password: <form:errors path="password0" /></td>
               <td><form:password path="password0" size="20" /></td>
          </tr>
          <tr>
               <td>Confirm password: <form:errors path="password1" /></td>
               <td><form:password path="password1" size="20" /></td>
          </tr>
          <tr>
               <td>e-mail: <form:errors path="email" /></td>
               <td><form:input path="email" size="30" /></td>
          </tr>
          <tr>
               <td>Forename: <form:errors path="forename" /></td>
               <td><form:input path="forename" size="20" /></td>
          </tr>
          <tr>
               <td>Surname: <form:errors path="surname" /></td>
               <td><form:input path="surname" size="20" /></td>
          </tr>
          <tr>
               <td colspan="2"><input type="submit" value="Ok" /></td>
          </tr>
     </table>


</form:form>


</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
     pageEncoding="UTF-8" import="org.prot.portal.login.*"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<title>Portal</title>
</head>
<body>

<p>Twospot login system</p>

<p>Login</p>
<form:form action="loginHandler.htm" method="POST" commandName="loginCommand">
     <form:hidden path="redirectUrl" />
     <form:hidden path="cancelUrl" />
     <table>
          <form:errors>
               <tr>
                    <td colspan="2">Invalid credentials</td>
               </tr>
          </form:errors>
          <tr>
               <td>Username: <form:errors path="username" /></td>
               <td><form:input path="username" size="60" /></td>
          </tr>
          <tr>
               <td>Password: <form:errors path="password" /></td>
               <td><form:password path="password" size="60" /></td>
          </tr>
          <tr>
               <td colspan="2"><input type="submit" value="Post" /></td>
          </tr>
     </table>
</form:form>

<ul>
     <li><a href="${loginCommand.cancelUrl}">Cancel</a></li>
</ul>

</body>
</html>
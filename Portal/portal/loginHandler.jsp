<%@ page language="java" contentType="text/html; charset=UTF-8"
     pageEncoding="UTF-8" import="org.prot.portal.login.*"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
<title>TwoSpot - Portal</title>
<link href="./etc/twospot.css" type="text/css" rel="stylesheet" />
</head>
<body>
<div class="loginField">
    <h2>TwoSpot login system:</h2>
    <form:form action="loginHandler.htm" method="POST" commandName="loginCommand">
        <form:hidden path="redirectUrl" />
        <form:hidden path="cancelUrl" />
        <table style="margin-top:10px">
            <form:errors>
                <tr>
                    <td colspan="2" class="error">Invalid credentials</td>
                </tr>
            </form:errors>
            <form:errors path="username">
                <tr>
                    <td colspan="2" class="error">Username is required</td>
                </tr>
            </form:errors>
            <form:errors path="password">
                <tr>
                    <td colspan="2" class="error">Password is required</td>
                </tr>
            </form:errors>
            <tr>
                <td>Username: </td>
                <td><form:input path="username" size="20" cssClass="loginField_input"/></td>
            </tr>
            <tr>
                <td>Password: </td>
                <td><form:password path="password" size="20" cssClass="loginField_input" /></td>
            </tr>
            <tr>
                <td colspan="2" align="right">
                <input type="button" onClick="parent.location='${loginCommand.cancelUrl}'" value="Cancel">
                    <input type="submit" value="Login" /></td>
            </tr>
        </table>
    </form:form>
</div>
</body>
</html>
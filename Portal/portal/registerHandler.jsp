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
<div id="header">
    <h1>TwoSpot - Portal</h1>
</div>
<div id="page"> <a href="/start.htm">Back</a>
    <p>Register a new user account:</p>
    <p class="hint">TwoSpot applications can only see you username. All other
        data remains within the TwoSpot portal.</p>
    <form:form action="registerHandler.htm" method="POST"
     commandName="registerCommand">
        <table>
            <form:errors path="username">
            <tr>
                <td colspan="2" class="error"><form:errors path="username" /></td>
            </tr>
            </form:errors>
            <form:errors path="password0">
            <tr>
                <td colspan="2" class="error"><form:errors path="password0" /></td>
            </tr>
            </form:errors>
            <form:errors path="password1">
            <tr>
                <td colspan="2" class="error"><form:errors path="password1" /></td>
            </tr>
            </form:errors>
            <form:errors path="email">
            <tr>
                <td colspan="2" class="error"><form:errors path="email" /></td>
            </tr>
            </form:errors>
            <form:errors path="forename">
            <tr>
                <td colspan="2" class="error"><form:errors path="forename" /></td>
            </tr>
            </form:errors>
            <form:errors path="surname">
            <tr>
                <td colspan="2" class="error"><form:errors path="surname" /></td>
            </tr>
            </form:errors>
            
            <tr>
                <td>Username:</td>
                <td><form:input path="username" size="15" /></td>
            </tr>
            <tr>
                <td>Password:</td>
                <td><form:password path="password0" size="20" /></td>
            </tr>
            <tr>
                <td>Confirm password:</td>
                <td><form:password path="password1" size="20" /></td>
            </tr>
            <tr>
                <td>e-mail:</td>
                <td><form:input path="email" size="30" /></td>
            </tr>
            <tr>
                <td>Forename:</td>
                <td><form:input path="forename" size="20" /></td>
            </tr>
            <tr>
                <td>Surname:</td>
                <td><form:input path="surname" size="20" /></td>
            </tr>
            <tr>
                <td colspan="2" align="right"><input type="submit" value="Ok" /><input type="submit" value="Cancel" onClick="window.document='/start.htm'" /></td>
            </tr>
        </table>
    </form:form>
</div>
</body>
</html>
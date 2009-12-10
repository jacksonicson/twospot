
<%@ page language="java" contentType="text/html; charset=UTF-8"
     pageEncoding="UTF-8" import="org.prot.portal.login.*"%>

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
<p>Create a new Application</p>
<form:form action="/registerApp.htm" method="POST"
     commandName="registerAppCommand">
     <table>
	     <form:errors path="appId">
                <tr>
                    <td colspan="2" class="error"><form:errors path="appId" /></td>
                </tr>
           </form:errors>
          <tr>
               <td>AppId:</td>
               <td><form:input path="appId" size="10" cssClass="appId_input"/></td>
          </tr>
          <tr>
               <td colspan="2" align="right"><input type="submit" value="Ok" /><input type="button" onClick="window.location='/portal.htm'" value="Back" /></td>
          </tr>
     </table>
</form:form>

</div>

</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="stripes"
     uri="http://stripes.sourceforge.net/stripes.tld"
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<title>Portal</title>
</head>
<body>


<stripes:form beanclass="org.prot.portal.CalculatorActionBean" focus="">
     <table>
          <tr>
               <td>Number 1:</td>
               <td><stripes:text name="numberOne" /></td>
          </tr>
          <tr>
               <td>Number 2:</td>
               <td><stripes:text name="numberTwo" /></td>
          </tr>
          <tr>
               <td colspan="2"><stripes:submit name="addition"
                    value="Add"
               /></td>
          </tr>
          <tr>
               <td>Result:${1 > (4/2)}</td>
               <%
               	System.out.println("test form jsp");
               %><!--
                <c:out value="${actionBean.result}"/>
                -->
               <td>${actionBean.result}</td>
          </tr>
     </table>
</stripes:form>
</body>
</html>
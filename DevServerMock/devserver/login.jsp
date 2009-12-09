<html>
<body>

<h1>DevServer - UserService (Mock) login page</h1>

<form action="./loginServlet" method="POST"><input type="hidden"
     name="okUrl" value='<%=request.getParameter("url") %>' /> <input
     type="hidden" name="errUrl" value='<%=request.getParameter("cancel") %>' />

<table>
     <tr>
          <td>Username:</td>
          <td><input type="text" name="username" size="60" /></td>
     </tr>
     <tr>
          <td colspan="2"><input type="submit" value="Post" /></td>
     </tr>
</table>
</form>


</body>
</html>
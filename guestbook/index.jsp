<%@ page import="java.util.List" %>
<%@ page import="guestbook.data.GuestEntry" %>

<html>
<body>
<h1>TwoSpot - Guestbook</h1>

<p><a href="create.jsp">Create an entry</a></p>

<table>

	<%
		List<GuestEntry> list = (List<GuestEntry>)request.getAttribute("list");
		for(GuestEntry entry : list)
		{
	%>

	<tr>
    	<td>
        	<%= entry.getName() %>
        </td>
    	<td>
        	<%= entry.getMessage() %>
        </td>
    </tr>
    
    <%
		}
	%>
    
   
</table>

</body>
</html>
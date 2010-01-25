<%@ page import="gwiki.data.WikiPage"%>

<html>
<body>
<h1>TwoSpot - GWiki</h1>

<%
     WikiPage ppage = ((WikiPage) request.getAttribute("page"));
     String encText = ((String) request.getAttribute("encText"));
%>

<h2>Page: <%=ppage.getTitle()%></h2>
<p>
     <%=encText%>
</p>

<form action="/save" method="post">
     <input type="hidden" name="pname" value="<%=ppage.getTitle()%>" />
     
     <textarea rows="10" cols="60" name="text"><%=ppage.getText()%></textarea>
     
     <input type="submit" />
</form>

</body>
</html>
<%@ page import="gwiki.data.WikiPage"%>


<%@page import="java.net.URLDecoder"%>
<%@page import="java.net.URLEncoder"%><html>
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
     <input type="hidden" name="pid" value="<% 
          if(ppage.getKey()==null) 
            out.write(""); 
          else
          {
            String encode = URLEncoder.encode(ppage.getKey().toString(), "UTF-8");
            out.write(encode);
          }
            
            %>" />
     <input type="hidden" name="pname" value="<%=ppage.getTitle()%>" />
     
     <textarea rows="10" cols="60" name="text"><%=ppage.getText()%></textarea>
     
     <input type="submit" />
</form>

<h3>Formatting</h3>
<p>
<ul>
<li>Wiki Links: ___pageName___</li>
<li>Headling 1: ===Headline 1===</li>
<li>Headling 2: ==Headline 1==</li>
<li>Headling 3: =Headline 1=</li>
</ul>
</p>

</body>
</html>
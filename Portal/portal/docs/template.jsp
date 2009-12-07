<%@ taglib uri='/WEB-INF/tlds/template.tld' prefix='template'%>
<html>
<head>
<title></title>
</head>
<body onload="prettyPrint()">

<link href="prettify.css" type="text/css" rel="stylesheet" />
<link href="twospot.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="prettify.js"></script>

<div id="header"><template:get name='header' /></div>

<div id="navigation"><template:get name='navigation' /></div>

<div id="page"><template:get name='page' /></div>

</body>
</html>
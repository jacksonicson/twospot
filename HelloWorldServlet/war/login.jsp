<html>
<head>
<title>Login</title>
</head>
<body>
<h1>Login</h1>
<form target="http://localhost:8080/portal/loginfinish"><!-- Hidden fields to transfer the redirect url -->
<input type="hidden" name="url" />
<p>Username: <input type="text" name="username" /></p>
<p>Password: <input type="password" name="password" /></p>
<p><input type="submit" /></p>
</form>
</html>
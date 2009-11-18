package org.prot.helloworldservlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet
{
	private static Logger logger = Logger.getLogger(LoginServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String okUrl = request.getParameter("url");

		if (okUrl == null)
		{
			response.sendError(404, "Missing redirect URL");
			return;
		}

		// Redirect to the login page
		String page = ""
				+ "<html>"
				+ "<head>"
				+ "<title>Login</title>"
				+ "</head>"
				+ "<body>"
				+ "<h1>Login</h1>"
				+ "<form action='http://localhost:8080/portal/loginfinish'><!-- Hidden fields to transfer the redirect url -->"
				+ "<input type='hidden' name='url' value='" + okUrl + "' />"
				+ "<p>Username: <input type='text' name='username' /></p>"
				+ "<p>Password: <input type='password' name='password' /></p>"
				+ "<p><input type='submit' /></p>" + "</form>" + "</html>";

		response.getWriter().print(page);
	}
}

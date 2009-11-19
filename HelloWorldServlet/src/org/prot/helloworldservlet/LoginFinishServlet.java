package org.prot.helloworldservlet;

import java.io.IOException;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.prot.appserver.services.UserService;
import org.prot.appserver.services.UserServiceFactory;

@SuppressWarnings("serial")
public class LoginFinishServlet extends HttpServlet
{
	private static Logger logger = Logger.getLogger(LoginFinishServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String okUrl = request.getParameter("url");
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		if (okUrl == null)
		{
			response.sendError(404, "Missing redirect URL");
			return;
		}
		
		// TODO: Check user credentials

		// Create a new user id 
		Random r = new Random();
		long l1 = r.nextLong();
		long l2 = r.nextLong();
		String uid = "_UID" + (l1 | l2) + "_";
		UserService service = UserServiceFactory.getUserService();
		response.getWriter().print(uid);

		// Register the sessionId
		service.registerUser(uid);

		// Save a platform cookie (is valid for all subdomains)
		Cookie cookie = new Cookie("UID", uid);
		cookie.setDomain("portal.mydomain");
		response.addCookie(cookie);

		// Redirect the client
		logger.debug("Login finished, redirecting to: " + okUrl);
		response.sendRedirect(okUrl);
	}
}

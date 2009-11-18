package org.prot.helloworldservlet;

import java.io.IOException;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.prot.appserver.config.Configuration;
import org.prot.appserver.services.UserServiceFactory;
import org.prot.appserver.services.UserServiceProxy;

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
		
		logger.debug("Redirect URL: " + okUrl + " Password: " + password + " Username: " + username); 
		
		// 
		Random r = new Random();
		long l1 = r.nextLong();
		long l2 = r.nextLong();
		String sessionId = "_UID" + (l1 | l2) + "_";
		UserServiceProxy service = (UserServiceProxy)UserServiceFactory.getUserService();
		response.getWriter().print(sessionId);

		// Register the sessionId
		service.registerSession(Configuration.getInstance().getAuthenticationToken(), sessionId);

		// Save a platform cookie (is valid for all subdomains)
		Cookie cookie = new Cookie("UID", sessionId);
//		cookie.setDomain(".localhost"); // TODO: Make this more general 
		response.addCookie(cookie);
		
		// Redirect the client
		response.sendRedirect(okUrl);
	}
}

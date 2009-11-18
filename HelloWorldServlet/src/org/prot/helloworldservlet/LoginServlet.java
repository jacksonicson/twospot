package org.prot.helloworldservlet;

import java.io.IOException;
import java.util.Random;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.prot.appserver.config.Configuration;
import org.prot.appserver.services.UserServiceFactory;
import org.prot.controller.services.user.PrivilegedUserService;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet
{

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String okUrl = request.getParameter("okUrl");
		String errorUrl = request.getParameter("errUrl");

		if (okUrl == null || errorUrl == null)
		{
			response.sendError(404, "Missing URL");
			return;
		}

		Random r = new Random();
		long l1 = r.nextLong();
		long l2 = r.nextLong();
		String sessionId = "TODO_" + (l1 | l2);
		PrivilegedUserService service = (PrivilegedUserService)UserServiceFactory.getUserService();
		response.getWriter().print(sessionId);

		// Register the sessionId
		service.registerSession(Configuration.getInstance().getAuthenticationToken(), sessionId);

		// Redirect the client
		response.sendRedirect(okUrl + "?sid=" + sessionId);
	}
}

package org.prot.devserver;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.prot.app.services.log.LogService;
import org.prot.app.services.log.LogServiceFactory;
import org.prot.app.services.user.UserService;
import org.prot.app.services.user.UserServiceFactory;

public class LoginServlet extends HttpServlet
{
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{
		UserService userService = UserServiceFactory.getUserService();
		LogService logService = LogServiceFactory.getLogService();

		String okUrl = request.getParameter("okUrl");
		String errUrl = request.getParameter("errUrl");
		String username = request.getParameter("username");

		boolean validUsername = true;
		if (username == null)
			validUsername = false;
		else if (username.length() < 3)
			validUsername = false;

		if (validUsername)
		{
			if (okUrl == null || okUrl.isEmpty())
			{
				response.getWriter().print("OkURL is null - UserService cannot redirect");
				return;
			} else
			{
				// Register user
				String identifier = "twospot_id_" + System.currentTimeMillis();
				userService.registerUser(identifier, username);

				// Set the UID cookie
				Cookie uidCookie = new Cookie("UID", identifier);
				response.addCookie(uidCookie);
				
				response.sendRedirect(okUrl);
				return;
			}
		} else
		{
			if (errUrl == null || errUrl.isEmpty())
			{
				response.getWriter().print("ErrURL is null - UserService cannot redirect");
				return;
			} else
			{
				response.sendRedirect(errUrl);
				return;
			}
		}
	}
}

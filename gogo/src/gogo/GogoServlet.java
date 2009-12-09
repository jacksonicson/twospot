package gogo;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.prot.app.services.log.LogService;
import org.prot.app.services.log.LogServiceFactory;
import org.prot.app.services.user.UserService;
import org.prot.app.services.user.UserServiceFactory;

public class GogoServlet extends HttpServlet
{
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{

		LogService logService = LogServiceFactory.getLogService();
		UserService userService = UserServiceFactory.getUserService();

		logService.info("Test logging");

		if (userService.getCurrentUser() == null)
		{
			logService.info("User is not logged in! - redirecting");
			String url = request.getRequestURL().toString();
			
			// response.sendRedirect(userService.getLoginUrl(url, url));
			
			// return;
		}

		logService.info("User is logged in: " + userService.getCurrentUser());

		PrintWriter out = response.getWriter();
		out.write("GoGo - Hello World");
	}
}

package org.prot.helloworldservlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.prot.app.services.UserServiceFactory;

@SuppressWarnings("serial")
public class HelloWorldServlet extends HttpServlet
{
	private static final Logger logger = Logger.getLogger(HelloWorldServlet.class);

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{

		// logger.info("CLASS:" +
		// this.getClass().getProtectionDomain().getCodeSource());

		// Darauf darf das ding garkeinen zugriff haben!!!
		// try
		// {
		// Configuration config = Configuration.getInstance();
		// System.out.println("Reading configuration: " +
		// config.getAppDirectory());
		// } catch (Exception e)
		// {
		// System.out.println("OK");
		// }
		// Configuration config = Configuration.getInstance();

		try
		{
			UserServiceFactory.getUserService();
		} catch (Exception e)
		{
			System.out.println("RMI stub failed");
			e.printStackTrace();
		}

		// Test the UserService
		// UserService service = UserServiceFactory.getUserService();
		// boolean value = service.getCurrentUser();
		// if (service.getCurrentUser() == false)
		// resp.sendRedirect(service.getLoginUrl(req.getRequestURL().toString()));
	}
}

package gogo;

import gogo.data.BlogEntry;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
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

			response.sendRedirect(userService.getLoginUrl(url, url));

			return;
		}

		logService.info("User is logged in: " + userService.getCurrentUser());

		// Use the DataStore
		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("etc/jdoDefault.properties");
		PersistenceManager manager = pmf.getPersistenceManager();
		Transaction tx = manager.currentTransaction();
		tx.begin();

		try
		{

			BlogEntry entry = new BlogEntry();
			entry.setUsername("test");
			entry.setMessage("test");

			manager.makePersistent(entry);

			tx.commit();
		} catch (Exception e)
		{
			tx.rollback();
		}

		PrintWriter out = response.getWriter();
		out.write("GoGo - Hello World");
	}
}

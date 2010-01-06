package gogo;

import gogo.data.BlogEntry;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
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
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{
		InputStream in = request.getInputStream();
		byte[] buffer = new byte[64];
		int len;
		while ((len = in.read(buffer)) > 0)
		{
			// Do nothing
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{
		// DEMO: Using the LogService
		LogService logService = LogServiceFactory.getLogService();
		logService.debug("Debug log");
		logService.info("Info log");
		logService.error("Error log");

		// DEMO: Using the UserService
		UserService userService = UserServiceFactory.getUserService();

		// First get some URL params
		String logout = request.getParameter("logout");

		// If the logout param is present - logout
		if (logout != null)
		{
			logService.debug("Unregistering user");
			userService.unregisterUser();
		}

		response.getWriter().print("Hello world");

		// Check if the user is logged in?
		if (userService.getCurrentUser() == null)
		{
			// User is _not_ logged in
			logService.info("User is not logged in! - redirecting");

			// Get the URL to the current servlet
			String url = request.getRequestURL().toString();

			// Ask the UserService for the login url. The UserService should
			// redirect to the current servlet after the login or if the
			// login fails!
			String loginUrl = userService.getLoginUrl(url, url);

			// Send the redirect
			response.sendRedirect(loginUrl);

			// Return
			return;
		}

		// The previous if block did not return therefore the user is logged in
		// Log his username.
		logService.info("User is logged in: " + userService.getCurrentUser());

		// DEMO: Use the DataStore for writing
		// Create a new PersistenceManagerFactory (Don't search for the
		// jdoDefault.properties it's builtin)
		// Creaet a new PersistenceManager
		PersistenceManager manager = DataConnection.getManager();

		try
		{
			// Get the current transaction
			Transaction tx = manager.currentTransaction();

			try
			{
				// Start a new transaction
				tx.begin();

				// Create a new object of BlogEntry
				BlogEntry entry = new BlogEntry();

				// Fill the object with some data
				entry.setUsername(userService.getCurrentUser());
				entry.setMessage("" + System.currentTimeMillis());

				// Save the object into the datastore
				manager.makePersistent(entry);

				// Commit the transaction
				tx.commit();
			} catch (Exception e)
			{
				logService.error(e.toString());

				// Rollback the transaction
				tx.rollback();
			} finally
			{
				// Check if transaction is still active, if yes rollback
				if (tx.isActive())
					tx.rollback();
			}

			// DEMO: Use the DataStore for reading
			// Create a new query
			Query query = manager.newQuery();

			// Tell the query the class
			query.setClass(BlogEntry.class);

			// Execute the query
			List<BlogEntry> result = (List<BlogEntry>) query.execute();

			// Print everything to the response
			PrintWriter out = response.getWriter();
			response.setContentType("text/html");
			out.print("<h1>GoGo-Datastore:</h1>");

			for (BlogEntry entry : result)
			{
				out.print("<p>");
				out.print("User: " + entry.getUsername() + " Message: " + entry.getMessage() + "\n\n");
				out.print("</p>");
			}
		} finally
		{
			// Finally close the persistence manager
			manager.close();
		}
	}
}

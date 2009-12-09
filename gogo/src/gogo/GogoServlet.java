package gogo;

import gogo.data.BlogEntry;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
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

		// Check if the user is logged in?
		if (userService.getCurrentUser() == null)
		{
			// User is _not_ logged in
			logService.info("User is not logged in! - redirecting");

			// Get the URL to the current servlet
			String url = request.getRequestURL().toString();

			// Ask the UserService for the login url. The UserService should
			// redirect to the current servlet after the login or if the login
			// fails!
			String loginUrl = userService.getLoginUrl(url, url);

			// Send the redirect
			response.sendRedirect(loginUrl);

			// Return
			return;
		}

		// The previous if block did not return therefore the user is logged in.
		// Log his username.
		logService.info("User is logged in: " + userService.getCurrentUser());

		// DEMO: Use the DataStore for writing
		// Create a new PersistenceManagerFactory (Don't search for the
		// jdoDefault.properties it's builtin)
		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("etc/jdoDefault.properties");

		// Creaet a new PersistenceManager
		PersistenceManager manager = pmf.getPersistenceManager();

		// Get the current transaction
		Transaction tx = manager.currentTransaction();

		// Start a new transaction
		tx.begin();

		try
		{
			// Create a new object of BlogEntry
			BlogEntry entry = new BlogEntry();

			// Fill the object with some data
			entry.setUsername(userService.getCurrentUser());
			entry.setMessage("blablabla: " + System.currentTimeMillis());

			// Save the object into the datastore
			manager.makePersistent(entry);

			// Commit the transaction
			tx.commit();
		} catch (Exception e)
		{
			logService.error(e.toString());

			// Rollback the transaction
			tx.rollback();
		}

		// DEMO: Use the DataStore for reading
		// Create a new query
		Query query = manager.newQuery();

		// Tell the query the class
		query.setClass(BlogEntry.class);

		// Read the first 300 entries
		query.setRange(0, 300);

		// Execute the query
		List<BlogEntry> result = (List<BlogEntry>) query.execute();

		// Print everything to the response
		PrintWriter out = response.getWriter();
		out.print("GoGo-Datastore: \n");

		for (BlogEntry entry : result)
		{
			out.print("Name: " + entry.getUsername() + " Message: " + entry.getMessage() + "\n\n");
		}
	}
}

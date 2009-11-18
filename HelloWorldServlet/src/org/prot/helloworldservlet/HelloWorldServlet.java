package org.prot.helloworldservlet;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.prot.appserver.services.UserServiceFactory;
import org.prot.controller.services.UserService;

@SuppressWarnings("serial")
public class HelloWorldServlet extends HttpServlet
{
	// TODO: Move this to the AppServer
	PersistenceManagerFactory pmf;
	PersistenceManager pm;

	public HelloWorldServlet()
	{
		// pmf =
		// JDOHelper.getPersistenceManagerFactory("/etc/jdoDefault.properties");
		// pm = pmf.getPersistenceManager();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{

		UserService service = UserServiceFactory.getUserService();
		service.getCurrentUser(); 
		String url = service.getLoginUrl(); 
		
		resp.setContentType("text/plain");
		resp.getWriter().println("helloWorld: " + url);
		resp.getWriter().close();

		// String t = "";
		// Transaction tx = pm.currentTransaction();
		// tx.begin();
		// Poll poll = new Poll("asdf", System.currentTimeMillis());
		// poll.setTest("test aaadfasfd");
		// pm.makePersistent(poll);
		// System.out.println("Made persistent - selecting all now");
		// t += "persistence";
		// tx.commit();
		//
		// tx.begin();
		// Query query = pm.newQuery(Poll.class);
		// Collection<Poll> allProducts = (Collection<Poll>) query.execute();
		//
		// for (Poll p : allProducts)
		// {
		// System.out.println("poll p : " + p.getPoll() + " test: " +
		// p.getTest());
		// t += " poll" + p.getTest();
		// }
		// tx.commit();
		//
		// resp.setContentType("text/plain");
		// resp.getWriter().println(t);
		// resp.getWriter().close();
	}
}

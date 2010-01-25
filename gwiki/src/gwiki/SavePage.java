package gwiki;

import gwiki.data.WikiPage;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SavePage extends HttpServlet
{
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{
		String pname = request.getParameter("pname");
		String text = request.getParameter("text");
		
		System.out.println("Pname: " + pname); 

		PersistenceManager manager = DataConnection.getManager();
		WikiPage page = DataConnection.fetchPage(manager, pname);

		if (page == null)
			page = new WikiPage();

		page.setTitle(pname);
		page.setText(text);

		manager.currentTransaction().setNontransactionalWrite(true);
		manager.currentTransaction().begin(); 
		manager.makePersistent(page);
		manager.currentTransaction().commit();
		manager.close();

		response.sendRedirect("/page?pname=" + pname);

	}
}

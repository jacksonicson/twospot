package gwiki;

import gwiki.data.WikiPage;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.prot.storage.Key;

public class SavePage extends HttpServlet
{
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{
		String pid = request.getParameter("pid");
		String pname = request.getParameter("pname");
		String text = request.getParameter("text");

		PersistenceManager manager = DataConnection.getManager();
		try
		{
			manager.currentTransaction().begin();

			WikiPage page = null;
			try
			{
				pid = URLDecoder.decode(pid, "UTF-8");
				page = (WikiPage) manager.getObjectById(new Key(pid));
			} catch (Exception e)
			{

			}

			if (page == null)
				page = new WikiPage();
			page.setTitle(pname);
			page.setText(text);
			manager.makePersistent(page);

			pid = page.getKey().toCompactString();
			pid = URLEncoder.encode(pid, "UTF-8");

			manager.currentTransaction().commit();

		} catch (Exception e)
		{

		} finally
		{
			if (manager.currentTransaction().isActive())
				manager.currentTransaction().rollback();
		}

		manager.close();

		response.sendRedirect("/page?pid=" + pid);
	}
}

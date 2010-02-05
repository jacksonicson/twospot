package gwiki;

import gwiki.data.WikiPage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.prot.storage.Key;

public class PageServlet extends HttpServlet
{
	private static final long serialVersionUID = -8491124727700643700L;

	

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{
		String pageId = request.getParameter("pid");
		PersistenceManager manager = DataConnection.getManager();

		WikiPage page = null;
		if (pageId != null)
		{
			try
			{
				page = (WikiPage) manager.getObjectById(new Key(pageId));
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		String pageName = request.getParameter("pname");
		if (page == null)
		{
			if (pageName == null)
			{
				response.sendError(404);
				return;
			}

			page = DataConnection.fetchPage(manager, pageName);
		}

		if (page == null)
		{
			page = new WikiPage();
			page.setTitle(pageName);
			page.setText("-empty-");
		}

		String target = "";
		String text = page.getText();
		Pattern p = Pattern.compile("___[a-zA-Z_0-9]+___");
		Matcher m = p.matcher(text);

		int last = 0;
		while (m.find())
		{
			String first = text.substring(last, m.start());
			String link = text.substring(m.start() + 3, m.end() - 3);
			last = m.end();

			target += first + "<a href='/page?pname=" + link + "'>" + link + "</a>";
		}

		target += text.substring(last);
		target = target.replaceAll("\n", "<br/>");

		request.setAttribute("page", page);
		request.setAttribute("encText", target);
		request.getRequestDispatcher("emptyPage.jsp").forward(request, response);

		manager.close();
	}
}

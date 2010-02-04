package gwiki;

import gwiki.data.WikiPage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

	private static final int length = 1024 * 200;
	private static byte[] data = new byte[length];

	static
	{
		Random r = new Random();
		r.nextBytes(data);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{

		try
		{
			Thread.sleep(10);
		} catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}

		long time = System.currentTimeMillis();
		ZipOutputStream out = new ZipOutputStream(new ByteArrayOutputStream(length));
		out.putNextEntry(new ZipEntry("blaaa"));
		out.write(data);
		out.close();
		time = System.currentTimeMillis() - time;
		System.out.println("Time: " + time);

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

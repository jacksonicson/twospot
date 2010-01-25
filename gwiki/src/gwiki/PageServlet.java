package gwiki;

import gwiki.data.WikiPage;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PageServlet extends HttpServlet
{
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{
		String pageId = request.getParameter("pid");
		String pageName = request.getParameter("pname");

		if (pageName == null)
		{
			response.sendError(404);
			return;
		}

		PersistenceManager manager = DataConnection.getManager();
		WikiPage page = DataConnection.fetchPage(manager, pageName);

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

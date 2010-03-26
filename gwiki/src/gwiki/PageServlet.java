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

		
		String text = page.getText();
		Pattern p0 = Pattern.compile("___[a-zA-Z_0-9]+___");
		Pattern p1 = Pattern.compile("===[a-zA-Z_0-9\\s]+===");
		Pattern p2 = Pattern.compile("==[a-zA-Z_0-9\\s]+==");
		Pattern p3 = Pattern.compile("=[a-zA-Z_0-9\\s]+=");
		text = replaceBy(p0, text, "<a href='/page?pname=" + "%v" + "'>" + "%v" + "</a>");
		text = replaceBy(p1, text, "<h2>%v</h2>");
		text = replaceBy(p2, text, "<h3>%v</h3>");
		text = replaceBy(p3, text, "<h4>%v</h4>");
		
		text= text.replaceAll("\n", "<br/>");
		
		request.setAttribute("page", page);
		request.setAttribute("encText", text);
		request.getRequestDispatcher("emptyPage.jsp").forward(request, response);

		manager.close();
	}
	
	private String replaceBy(Pattern p, String s, String r)
	{
		Matcher m = p.matcher(s);
		String target = "";
		int last = 0;
		while (m.find())
		{
			String first = s.substring(last, m.start());
			String link = s.substring(m.start() + 3, m.end() - 3);
			last = m.end();
			
			r = r.replaceAll("%v", link);
			target += first + r; 
		}

		target += s.substring(last);
		
		return target; 
	}
}

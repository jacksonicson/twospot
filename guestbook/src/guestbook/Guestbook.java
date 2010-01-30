package guestbook;

import guestbook.data.GuestEntry;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Guestbook extends HttpServlet
{
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{
		PersistenceManager manager = DataConnection.getManager();

		long time = System.currentTimeMillis() - 1000;

		Query query = manager.newQuery();
		query.setClass(GuestEntry.class);
		query.setFilter("timestamp > " + time);
		query.setOrdering("timestamp");
		query.setRange(0, 10);

		List<GuestEntry> result = (List<GuestEntry>) query.execute();

		manager.close();

		request.setAttribute("list", result);
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}
}

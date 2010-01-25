package gwiki;

import gwiki.data.WikiPage;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

public class DataConnection
{
	private static PersistenceManagerFactory pmf;

	public static PersistenceManager getManager()
	{
		if (pmf == null)
		{
			pmf = JDOHelper.getPersistenceManagerFactory("etc/jdoDefault.properties");
		}

		return pmf.getPersistenceManager();
	}

	public static WikiPage fetchPage(PersistenceManager manager, String pname)
	{
		Query query = manager.newQuery(WikiPage.class);
		query.setFilter("title == '" + pname + "'");
		query.setUnique(true);

		WikiPage page = (WikiPage) query.execute();
		return page;
	}
}

package guestbook;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

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
}

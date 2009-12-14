package org.prot.util.jdo;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

public class JdoConnection
{
	private static PersistenceManagerFactory pmf;

	public static PersistenceManager getPersistenceManager()
	{
		if (pmf == null)
		{
			pmf = JDOHelper.getPersistenceManagerFactory("etc/jdoDefault.properties");
		}

		return pmf.getPersistenceManager();
	}
}

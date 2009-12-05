package org.prot.app.services.log;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

public class JdoConnection
{
	private static PersistenceManagerFactory pmf;
	private static PersistenceManager pm;

	public void init()
	{
		pmf = JDOHelper.getPersistenceManagerFactory("etc/jdoDefault.properties");
		pm = pmf.getPersistenceManager();
	}

	public PersistenceManager getPersistenceManager()
	{
		return pm;
	}
}

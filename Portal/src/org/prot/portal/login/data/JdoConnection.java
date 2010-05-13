package org.prot.portal.login.data;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

public class JdoConnection
{
	private static PersistenceManagerFactory pmf;
	private static PersistenceManager pm;

	public void init()
	{
		ClassLoader loader = this.getClass().getClassLoader();
		pmf = JDOHelper.getPersistenceManagerFactory("etc/jdoDefault.properties", loader, loader);

	}

	public PersistenceManager getPersistenceManager()
	{
		return pmf.getPersistenceManager();
	}
}

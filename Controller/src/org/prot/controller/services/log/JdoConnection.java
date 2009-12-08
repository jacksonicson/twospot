package org.prot.controller.services.log;

import java.util.Stack;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

public class JdoConnection
{
	private static PersistenceManagerFactory pmf;

	private Stack<PersistenceManager> pms = new Stack<PersistenceManager>();

	public void init()
	{
		pmf = JDOHelper.getPersistenceManagerFactory("etc/jdoDefault.properties");
	}

	public synchronized PersistenceManager getPersistenceManager()
	{
		if (pms.isEmpty())
			return pmf.getPersistenceManager();

		return pms.pop();
	}

	public synchronized void releasePersistenceManager(PersistenceManager pm)
	{
		pms.push(pm);
	}
}

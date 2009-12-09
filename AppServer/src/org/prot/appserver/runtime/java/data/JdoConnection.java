package org.prot.appserver.runtime.java.data;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.apache.log4j.Logger;

public class JdoConnection
{
	private static Logger logger = Logger.getLogger(JdoConnection.class);

	private static PersistenceManagerFactory pmf;
	private static PersistenceManager pm;

	public PersistenceManager getPersistenceManager()
	{
		if (pmf == null)
		{
			logger.debug("Creating peristence manager factory");
			pmf = JDOHelper.getPersistenceManagerFactory("etc/jdoDefault.properties");

			logger.debug("Creating persistence manager");
			pm = pmf.getPersistenceManager();

			logger.debug("Persistence done");
		}

		return pm;
	}
}

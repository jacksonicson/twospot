package org.prot.util.jdo;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

/**
 * @author Andreas Wolke
 */
public class JdoConnection {
	private static PersistenceManagerFactory pmf;

	/**
	 * Creates a new PersistenceManagerFactory and configures it using a
	 * properties file
	 * 
	 * @return a configured PersistenceManagerFactory
	 */
	public static PersistenceManager getPersistenceManager() {
		if (pmf == null) {
			pmf = JDOHelper
					.getPersistenceManagerFactory("etc/jdoDefault.properties");
		}

		return pmf.getPersistenceManager();
	}
}

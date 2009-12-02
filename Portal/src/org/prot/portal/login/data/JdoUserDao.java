package org.prot.portal.login.data;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;

public class JdoUserDao implements UserDao
{
	private static final Logger logger = Logger.getLogger(JdoUserDao.class);

	private JdoConnection jdoConnection;

	@Override
	public PlatformUser getUser(String username)
	{
		PersistenceManager pm = jdoConnection.getPersistenceManager();

		Query query = pm.newQuery();
		query.setClass(PlatformUser.class);
		query.setFilter("username == '" + username + "'");
		query.setUnique(true);
		
		try
		{
			PlatformUser user = (PlatformUser) query.execute();
			return user;

		} catch (Exception e)
		{
			logger.error("Unable to fetch user", e);
		}

		return null;
	}

	@Override
	public void saveUser(PlatformUser user)
	{
		PersistenceManager pm = jdoConnection.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		tx.begin();

		try
		{
			logger.info("Making persistent"); 
			pm.makePersistent(user);
			logger.info("done");
			tx.commit();
		} catch (Exception e)
		{
			logger.info("Making persistent error", e); 
			tx.rollback();
		}
	}

	@Override
	public void updateUser(PlatformUser user)
	{
		saveUser(user);
	}

	public void setJdoConnection(JdoConnection jdoConnection)
	{
		this.jdoConnection = jdoConnection;
	}

}

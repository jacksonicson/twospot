package org.prot.controller.services.user;

import java.util.Collection;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;

public class UserServiceImpl implements PrivilegedUserService
{
	private static final Logger logger = Logger.getLogger(UserServiceImpl.class);
	
	private PersistenceManagerFactory pmFactory;
	private PersistenceManager persistenceManager;

	public UserServiceImpl()
	{
		pmFactory = JDOHelper.getPersistenceManagerFactory("etc/jdoDefault.properties");
		persistenceManager = pmFactory.getPersistenceManager();
	}

	@Override
	public boolean getCurrentUser(String session)
	{
		if (session == null)
			return false;

		// Query database
		Query query = persistenceManager.newQuery(UserSession.class);
		Collection<UserSession> result = (Collection<UserSession>) query.execute();
		for (UserSession test : result)
		{
			if (test.getSessionId().equals(session))
				return true;
		}

		return false;
	}

	@Override
	public String getLoginUrl()
	{
		return "http://localhost:8080/helloworld/login";
	}

	@Override
	public void registerSession(String token, String session)
	{
		// TODO: Use AOP to check the token!
		logger.info("Supplied token: " + token);

		UserSession userSession = new UserSession();
		userSession.setSessionId(session);

		Transaction tx = persistenceManager.currentTransaction();
		tx.begin();
		try
		{
			persistenceManager.makePersistent(userSession);
			tx.commit();
		} catch (Exception e)
		{
			logger.error("Could not persist user session", e);
			tx.rollback();
		}
	}
}
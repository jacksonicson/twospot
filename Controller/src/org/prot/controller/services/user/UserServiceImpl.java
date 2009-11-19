package org.prot.controller.services.user;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;

public class UserServiceImpl implements UserService
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
	public boolean getCurrentUser(String uid)
	{
		if (uid == null)
			return false;

		// Query database
		Query query = persistenceManager.newQuery(UserSession.class);
		Collection<UserSession> result = (Collection<UserSession>) query.execute();
		for (UserSession test : result)
		{
			if (test.getSessionId().equals(uid))
				return true;
		}

		return false;
	}

	@Override
	public String getLoginUrl(String redirectUrl)
	{
		// TODO: Configuration
		String url = "http://portal.mydomain:6060";
		String uri = "";
		try
		{
			uri = "/login?url=" + URLEncoder.encode(redirectUrl, "UTF-8");
		} catch (UnsupportedEncodingException e)
		{
			logger.error("", e);
		}

		return url + uri;
	}

	@Override
	public synchronized void registerUser(String token, String session)
	{
		// TODO: Check the token

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
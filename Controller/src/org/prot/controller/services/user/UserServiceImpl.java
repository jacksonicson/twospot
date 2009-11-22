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
	public String getCurrentUser(String uid)
	{
		assert (uid != null);

		// Query database
		Query query = persistenceManager.newQuery(UserSession.class);
		query.setFilter("sessionId == '" + uid + "'");
		query.setUnique(true);

		UserSession session = (UserSession) query.execute();
		if (session != null)
		{
			logger.warn("User session is not null");
			return session.getUsername();
		}

		logger.warn("Could not find a user session for: " + uid);
		return null;
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
	public synchronized void registerUser(String token, String session, String username)
	{
		// TODO: Make this more generic
		// TODO: Check token

		// Create a new UserSession object
		UserSession userSession = new UserSession();
		userSession.setSessionId(session);
		userSession.setUsername(username);

		Transaction tx = persistenceManager.currentTransaction();
		try
		{
			// Delete everything from previous sessions
			tx.begin();
			Query query = persistenceManager.newQuery(UserSession.class);
			query.setFilter("username == '" + username + "'");
			Collection<UserSession> oldSessions = (Collection<UserSession>)query.execute();
			for(UserSession delete : oldSessions)
				persistenceManager.deletePersistent(delete);
			tx.commit();
			
			// Create a new entry for this session
			tx.begin();
			persistenceManager.makePersistent(userSession);
			logger.debug("User session is persistent");
			tx.commit();
			
		} catch (Exception e)
		{
			logger.error("Could not persistate user session", e);
			tx.rollback();
		}
	}

	@Override
	public void unregisterUser(String uid)
	{
		assert (uid != null);

		// Query database
		Query query = persistenceManager.newQuery(UserSession.class);
		query.setFilter("sessionId == '" + uid + "'");
		query.setUnique(true);

		UserSession session = (UserSession) query.execute();
		if (session != null)
			persistenceManager.deletePersistent(session);
	}
}
package org.prot.controller.services.user;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;
import org.prot.controller.config.Configuration;
import org.prot.controller.manager.AppManager;
import org.prot.controller.services.log.JdoConnection;
import org.prot.util.ReservedAppIds;

public class UserServiceImpl implements UserService
{
	private static final Logger logger = Logger.getLogger(UserServiceImpl.class);

	private AppManager appManager;

	private PersistenceManagerFactory pmFactory;

	private JdoConnection jdoConnection;

	public UserServiceImpl()
	{
		jdoConnection = new JdoConnection();
	}

	@Override
	public synchronized String getCurrentUser(String uid)
	{
		assert (uid != null);

		// Query database
		PersistenceManager persistenceManager = jdoConnection.getPersistenceManager();
		try
		{
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
		} finally
		{
			jdoConnection.releasePersistenceManager(persistenceManager);
		}
	}

	@Override
	public String getLoginUrl(String redirectUrl, String cancelUrl)
	{
		String url = "http://" + ReservedAppIds.APP_PORTAL + "."
				+ Configuration.getConfiguration().getPlatformDomain();
		String uri = "";
		try
		{
			uri += "/login.htm?url=" + URLEncoder.encode(redirectUrl, "UTF-8");
			uri += "&cancel=" + URLEncoder.encode(cancelUrl, "UTF-8");
		} catch (UnsupportedEncodingException e)
		{
			// Return null - we don't want to direct the user to invalid
			// redirect or cancel urls
			return null;
		}

		url = url + uri;
		logger.debug("Built login url: " + url);

		return url;
	}

	@Override
	public synchronized void registerUser(String token, String session, String username)
	{
		if (appManager.checkToken(token) == false)
			return;

		// Create a new UserSession object
		UserSession userSession = new UserSession();
		userSession.setSessionId(session);
		userSession.setUsername(username);

		PersistenceManager persistenceManager = jdoConnection.getPersistenceManager();
		try
		{
			Transaction tx = persistenceManager.currentTransaction();
			try
			{
				// Delete everything from previous sessions
				tx.begin();
				Query query = persistenceManager.newQuery(UserSession.class);
				query.setFilter("username == '" + username + "'");
				Collection<UserSession> oldSessions = (Collection<UserSession>) query.execute();
				for (UserSession delete : oldSessions)
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
		} finally
		{
			jdoConnection.releasePersistenceManager(persistenceManager);
		}
	}

	@Override
	public void unregisterUser(String token, String uid)
	{
		if (appManager.checkToken(token) == false)
			return;

		assert (uid != null);

		PersistenceManager persistenceManager = jdoConnection.getPersistenceManager();
		try
		{
			// Query database
			Query query = persistenceManager.newQuery(UserSession.class);
			query.setFilter("sessionId == '" + uid + "'");
			query.setUnique(true);

			UserSession session = (UserSession) query.execute();
			if (session != null)
				persistenceManager.deletePersistent(session);
		} finally
		{
			jdoConnection.releasePersistenceManager(persistenceManager);
		}
	}

	public void setAppManager(AppManager appManager)
	{
		this.appManager = appManager;
	}
}
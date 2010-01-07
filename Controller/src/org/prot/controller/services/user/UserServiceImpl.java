package org.prot.controller.services.user;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;
import org.prot.controller.app.TokenChecker;
import org.prot.controller.config.Configuration;
import org.prot.util.ReservedAppIds;
import org.prot.util.jdo.JdoConnection;

public class UserServiceImpl implements UserService
{
	private static final Logger logger = Logger.getLogger(UserServiceImpl.class);

	private TokenChecker tokenChecker;

	@Override
	public String getCurrentUser(String uid)
	{
		if (uid == null)
			return null;

		// Query database
		PersistenceManager persistenceManager = JdoConnection.getPersistenceManager();

		try
		{
			Query query = persistenceManager.newQuery(UserSession.class);
			query.setFilter("sessionId == '" + uid.trim() + "'");
			query.setUnique(true);

			Object object = query.execute();

			if (object != null)
			{
				UserSession session = (UserSession) object;
				String user = session.getUsername();
				return user;
			} else
			{
				logger.debug("Could not find user: " + uid);
				return null;
			}
		} catch (Exception e)
		{
			logger.error("Error while fetching user", e);
			return null;
		} finally
		{
			persistenceManager.close();
		}
	}

	@Override
	public String getLoginUrl(String redirectUrl, String cancelUrl)
	{
		StringBuilder url = new StringBuilder();
		url.append("http://");
		url.append(ReservedAppIds.APP_PORTAL);
		url.append(".");
		url.append(Configuration.getConfiguration().getPlatformDomain());
		try
		{
			url.append("/login.htm?url=" + URLEncoder.encode(redirectUrl, "UTF-8"));
			url.append("&cancel=" + URLEncoder.encode(cancelUrl, "UTF-8"));
		} catch (UnsupportedEncodingException e)
		{
			// Return null - we don't want to direct the user to invalid
			// redirect or cancel urls
			return null;
		}

		return url.toString();
	}

	@Override
	public synchronized void registerUser(String token, String session, String username)
	{
		if (tokenChecker.checkToken(token) == false)
			return;

		// Create a new UserSession object
		UserSession userSession = new UserSession();
		userSession.setSessionId(session);
		userSession.setUsername(username);

		PersistenceManager persistenceManager = JdoConnection.getPersistenceManager();
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
			} finally
			{
				if (tx.isActive())
					tx.rollback();
			}

		} finally
		{
			persistenceManager.close();
		}
	}

	@Override
	public void unregisterUser(String uid)
	{
		if (uid == null)
			return;

		logger.debug("Unregistering user: " + uid);

		PersistenceManager persistenceManager = JdoConnection.getPersistenceManager();
		try
		{
			// Query database
			Query query = persistenceManager.newQuery(UserSession.class);
			query.setFilter("sessionId == '" + uid + "'");
			query.setUnique(true);

			UserSession session = (UserSession) query.execute();
			if (session != null)
			{
				logger.debug("Removing session");
				persistenceManager.currentTransaction().begin();
				persistenceManager.deletePersistent(session);
				persistenceManager.currentTransaction().commit();
			} else
			{
				logger.warn("Could not find user session: " + uid);
			}

		} catch (Exception e)
		{
			persistenceManager.currentTransaction().rollback();
			logger.error("Could not unregister user", e);
		} finally
		{
			persistenceManager.close();
		}
	}

	public void setTokenChecker(TokenChecker tokenChecker)
	{
		this.tokenChecker = tokenChecker;
	}
}
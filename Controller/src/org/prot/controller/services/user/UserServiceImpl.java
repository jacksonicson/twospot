package org.prot.controller.services.user;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

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
		{
			logger.warn("Invalid AppServer token");
			return;
		}

		// Create a new UserSession object
		UserSession userSession = new UserSession();
		userSession.setSessionId(session);
		userSession.setUsername(username);
		userSession.setTimestamp(System.currentTimeMillis());

		PersistenceManager persistenceManager = JdoConnection.getPersistenceManager();
		try
		{
			try
			{
				// Delete everything from previous sessions
				Query query = persistenceManager.newQuery(UserSession.class);
				query.setFilter("username == '" + username + "'");
				ArrayList<UserSession> oldSessions = (ArrayList<UserSession>) query.execute();
				for (UserSession oldSession : oldSessions)
				{
					long timestamp = oldSession.getTimestamp();
					timestamp = System.currentTimeMillis() - timestamp;
					if (timestamp < 0 || timestamp > 24 * 60 * 60 * 1000)
					{
						logger.debug("Removing old session");
						persistenceManager.deletePersistent(oldSession);
					}
				}

				// Create a new entry for this session
				persistenceManager.makePersistent(userSession);
				logger.debug("User session is persistent");

			} catch (Exception e)
			{
				logger.error("Could not persistate user session", e);
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
		{
			logger.warn("Invalid user identifier token");
			return;
		}

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
				persistenceManager.currentTransaction().begin();
				persistenceManager.deletePersistent(session);
				persistenceManager.currentTransaction().commit();
			} else
			{
				logger.warn("Could not find and delete user session: " + uid);
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
package org.prot.appserver.runtime.java.data;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;
import org.prot.appserver.runtime.java.SessionData;
import org.prot.util.jdo.JdoConnection;

public class JdoSessionDao implements SessionDao
{
	private static final Logger logger = Logger.getLogger(JdoSessionDao.class);

	private JdoConnection connection;

	@Override
	public boolean exists(String sessionId)
	{
		PersistenceManager pm = connection.getPersistenceManager();

		Query query = pm.newQuery();
		query.setClass(SessionData.class);
		query.setFilter("sessionId == '" + sessionId + "'");
		query.setUnique(true);

		try
		{
			SessionData data = (SessionData) query.execute();
			logger.debug("exists: " + (data != null));

			return (data != null);
		} catch (Exception e)
		{
			logger.error("Could not load SessionData: " + sessionId, e);
		}

		return false;
	}

	@Override
	public boolean isStale(String sessionId, long timestamp)
	{
		PersistenceManager pm = connection.getPersistenceManager();
		Query query = pm.newQuery();
		query.setClass(SessionData.class);
		query.setFilter("sessionId == '" + sessionId + "'");
		query.setUnique(true);

		try
		{
			logger.debug("loading stale state");
			SessionData data = (SessionData) query.execute();
			logger.debug("stale: " + data);

			if (data == null)
				return true;

			return (timestamp - data.getLastAccessed()) < 0f;

		} catch (Exception e)
		{
			logger.error("Could not load SessionData: " + sessionId, e);
		}

		return true;
	}

	@Override
	public SessionData loadSession(String sessionId)
	{
		PersistenceManager pm = connection.getPersistenceManager();
		Query query = pm.newQuery();
		query.setClass(SessionData.class);
		query.setFilter("sessionId == '" + sessionId + "'");
		query.setUnique(true);

		try
		{
			logger.debug("loading session");
			SessionData data = (SessionData) query.execute();
			logger.debug("done: " + data.getClass());

			logger.debug("restoring session");
			data.restoreSerialization();

			return data;
		} catch (Exception e)
		{
			logger.error("Could not load SessionData: " + sessionId, e);
		}

		return null;
	}

	@Override
	public void saveSession(SessionData sessionData)
	{
		PersistenceManager pm = connection.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		tx.begin();
		try
		{
			logger.debug("preparing session");
			sessionData.prepareSerialization();

			logger.debug("persisting session");
			pm.makePersistent(sessionData);
			logger.debug("done");

			tx.commit();
		} catch (Exception e)
		{
			logger.error("Could not save the session: " + sessionData.getSessionId());
			tx.rollback();
		}
	}

	@Override
	public void deleteSession(SessionData sessionData)
	{
		PersistenceManager pm = connection.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		tx.begin();
		try
		{
			logger.debug("deleting session");
			pm.deletePersistent(sessionData);
			logger.debug("done");

			tx.commit();
		} catch (Exception e)
		{
			logger.error("Could not save the session: " + sessionData.getSessionId());
			tx.rollback();
		}
	}

	@Override
	public void updateSession(SessionData sessionData)
	{
		PersistenceManager pm = connection.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		tx.begin();
		try
		{
			logger.debug("preparing session");
			sessionData.prepareSerialization();

			logger.debug("updating session");
			pm.makePersistent(sessionData);
			logger.debug("done");

			tx.commit();
		} catch (Exception e)
		{
			logger.error("Could not save the session: " + sessionData.getSessionId());
			tx.rollback();
		}
	}

	@Override
	public void addSessionId(String sessionId)
	{
		// Creating transferable object
		SessionId id = new SessionId(sessionId);

		PersistenceManager pm = connection.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		tx.begin();
		try
		{
			logger.debug("persisting sessionId");
			pm.makePersistent(id);
			logger.debug("done");

			tx.commit();
		} catch (Exception e)
		{
			logger.error("Could not save the sessionId: " + sessionId);
			tx.rollback();
		}
	}

	@Override
	public void deleteSessionId(String sessionId)
	{
		SessionId id = new SessionId(sessionId);

		PersistenceManager pm = connection.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		tx.begin();
		try
		{
			logger.debug("deleting session");
			pm.deletePersistent(id);
			logger.debug("done");

			tx.commit();
		} catch (Exception e)
		{
			logger.error("Could not save the sessionId: " + sessionId);
			tx.rollback();
		}
	}

	@Override
	public boolean existsSessionId(String sessionId)
	{
		PersistenceManager pm = connection.getPersistenceManager();

		Query query = pm.newQuery();
		query.setClass(SessionId.class);
		query.setFilter("sessionId == '" + sessionId + "'");
		query.setUnique(true);

		try
		{
			SessionId data = (SessionId) query.execute();
			logger.debug("exists sessionId: " + (data != null));

			return (data != null);

		} catch (Exception e)
		{
			logger.error("Could not load SessionId: " + sessionId, e);
		}

		return false;
	}

	public synchronized void setConnection(JdoConnection connection)
	{
		this.connection = connection;
	}
}

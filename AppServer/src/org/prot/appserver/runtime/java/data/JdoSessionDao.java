/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
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

	@Override
	public boolean exists(String sessionId)
	{
		PersistenceManager pm = JdoConnection.getPersistenceManager();

		try
		{
			Query query = pm.newQuery();
			query.setClass(SessionData.class);
			query.setFilter("sessionId == '" + sessionId + "'");
			query.setUnique(true);

			try
			{
				SessionData data = (SessionData) query.execute();
				return (data != null);

			} catch (Exception e)
			{
				logger.error("Could not load SessionData", e);
			}

		} finally
		{
			pm.close();
		}

		return false;
	}

	@Override
	public boolean isStale(String sessionId, long timestamp)
	{
		PersistenceManager pm = JdoConnection.getPersistenceManager();

		try
		{
			Query query = pm.newQuery();
			query.setClass(SessionData.class);
			query.setFilter("sessionId == '" + sessionId + "'");
			query.setUnique(true);

			SessionData data = (SessionData) query.execute();

			if (data == null)
				return true;

			return (timestamp - data.getLastAccessed()) < 0f;

		} catch (Exception e)
		{
			logger.error("Could not load SessionData", e);
		} finally
		{
			pm.close();
		}

		return true;
	}

	@Override
	public SessionData loadSession(String sessionId)
	{
		PersistenceManager pm = JdoConnection.getPersistenceManager();

		try
		{
			Query query = pm.newQuery();
			query.setClass(SessionData.class);
			query.setFilter("sessionId == '" + sessionId + "'");
			query.setUnique(true);

			SessionData data = (SessionData) query.execute();

			data.restoreSerialization();

			return data;
		} catch (Exception e)
		{
			logger.error("Could not load SessionData", e);
		} finally
		{
			pm.close();
		}

		return null;
	}

	@Override
	public void saveSession(SessionData sessionData)
	{
		PersistenceManager pm = JdoConnection.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try
		{
			tx.begin();

			sessionData.prepareSerialization();
			pm.makePersistent(sessionData);

			tx.commit();
		} catch (Exception e)
		{
			logger.error("Could not save the session", e);
			tx.rollback();

		} finally
		{
			if (tx.isActive())
				tx.rollback();

			pm.close();
		}
	}

	@Override
	public void deleteSession(SessionData sessionData)
	{
		PersistenceManager pm = JdoConnection.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try
		{
			tx.begin();

			pm.deletePersistent(sessionData);

			tx.commit();
		} catch (Exception e)
		{
			logger.error("Could not save the session", e);
			tx.rollback();
		} finally
		{
			if (tx.isActive())
				tx.rollback();

			pm.close();
		}
	}

	@Override
	public void updateSession(SessionData sessionData)
	{
		PersistenceManager pm = JdoConnection.getPersistenceManager();

		Transaction tx = pm.currentTransaction();
		try
		{
			tx.begin();

			sessionData.prepareSerialization();
			pm.makePersistent(sessionData);

			tx.commit();
		} catch (Exception e)
		{
			logger.error("Could not save the session", e);
			tx.rollback();

		} finally
		{
			if (tx.isActive())
				tx.rollback();

			pm.close();
		}
	}

	@Override
	public void addSessionId(String sessionId)
	{
		// Creating transferable object
		SessionId id = new SessionId(sessionId);

		PersistenceManager pm = JdoConnection.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try
		{
			tx.begin();

			pm.makePersistent(id);

			tx.commit();
		} catch (Exception e)
		{
			logger.error("Could not save the sessionId", e);
			tx.rollback();
		} finally
		{

			if (tx.isActive())
				tx.rollback();

			pm.close();
		}
	}

	@Override
	public void deleteSessionId(String sessionId)
	{
		SessionId id = new SessionId(sessionId);

		PersistenceManager pm = JdoConnection.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try
		{
			tx.begin();

			pm.deletePersistent(id);

			tx.commit();
		} catch (Exception e)
		{
			logger.error("Could not save the sessionId", e);
			tx.rollback();
		} finally
		{
			if (tx.isActive())
				tx.rollback();

			pm.close();
		}
	}

	@Override
	public boolean existsSessionId(String sessionId)
	{
		PersistenceManager pm = JdoConnection.getPersistenceManager();

		try
		{
			Query query = pm.newQuery();
			query.setClass(SessionId.class);
			query.setFilter("sessionId == '" + sessionId + "'");
			query.setUnique(true);

			SessionId data = (SessionId) query.execute();

			return (data != null);

		} catch (Exception e)
		{
			logger.error("Could not load SessionId", e);

		} finally
		{
			pm.close();
		}

		return false;
	}
}

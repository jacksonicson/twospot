package org.prot.appserver.runtime.java;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.AbstractSessionManager;
import org.eclipse.jetty.util.LazyList;

public class DistributedSessionManager extends AbstractSessionManager
{
	private static final Logger logger = Logger.getLogger(DistributedSessionManager.class);

	private ConcurrentHashMap<String, DistributedSession> sessions;

	class DistributedSession extends Session
	{
		private SessionData data;

		private boolean dirty = false;

		protected DistributedSession(HttpServletRequest request)
		{
			super(request);

			data = new SessionData(super._clusterId);
			data.setMaxIdleMs(_dftMaxIdleSecs * 1000);
			data.setCanonicalContext(canonicalize(_context.getContextPath()));
			data.setVirtualHost(getVirtualHost(_context));
			data.setExpiryTime(_maxIdleMs < 0 ? 0 : (System.currentTimeMillis() + _maxIdleMs));

			super._values = data.getAttributes();
		}

		protected DistributedSession(SessionData data)
		{
			super(data.getCreated(), data.getSessionId());
			this.data = data;
			super._values = data.getAttributes();
		}
		
		public void updateSessionData(SessionData data)
		{
			this.data = data;
		}

		public SessionData getSessionData()
		{
			return this.data;
		}

		protected void didActivate()
		{
			super.didActivate();
		}

		protected void willPassivate()
		{
			super.willPassivate();
		}

		protected String getClusterId()
		{
			return super.getClusterId();
		}

		@Override
		protected Map newAttributeMap()
		{
			return data.getAttributes();
		}

		public void setAttribute(String name, Object value)
		{
			super.setAttribute(name, value);
			dirty = true;
		}

		public void removeAttribute(String name)
		{
			super.removeAttribute(name);
			dirty = true;
		}

		protected void cookieSet()
		{
			data.setCookieSet(data.getAccessed());
		}

		protected void access(long time)
		{
			super.access(time);
			data.setLastAccessed(data.getAccessed());
			data.setAccessed(time);
			data.setExpiryTime(_maxIdleMs < 0 ? 0 : (time + _maxIdleMs));
		}

		protected void complete()
		{
			super.complete();
			try
			{
				if (dirty)
				{
					willPassivate();
					// updateSession(data);
					didActivate();
				}
			} catch (Exception e)
			{
				logger.warn("Problem persisting changed session data id = " + getId(), e);
			} finally
			{
				dirty = false;
			}
		}

		private String getVirtualHost(ContextHandler.Context context)
		{
			String vhost = "0.0.0.0";

			if (context == null)
				return vhost;

			String[] vhosts = context.getContextHandler().getVirtualHosts();
			if (vhosts == null || vhosts.length == 0 || vhosts[0] == null)
				return vhost;

			return vhosts[0];
		}

		private String canonicalize(String path)
		{
			if (path == null)
				return "";

			return path.replace('/', '_').replace('.', '_').replace('\\', '_');
		}
	}

	PersistenceManagerFactory pmf;
	PersistenceManager pm;

	private void init()
	{
		if (pmf == null)
		{
			pmf = JDOHelper.getPersistenceManagerFactory("/etc/jdoDefault.properties");
			pm = pmf.getPersistenceManager();
		}
	}

	private void storeSession(DistributedSession session)
	{
		init();

		Transaction tx = pm.currentTransaction();

		SessionData data = session.getSessionData();
		try
		{
			data.prepareSerialization();

			tx.begin();
			pm.makePersistent(data);
			tx.commit();

			logger.info("txtx made persistent");

		} catch (IOException e)
		{
			tx.rollback();

			e.printStackTrace();
		}
	}

	private DistributedSession loadSession(DistributedSession target, String idInCluster)
	{
		init();

		logger.info("txtx loading session");

		Query query = pm.newQuery();

		logger.info("txtx query");

		query.setClass(SessionData.class);
		query.setFilter("sessionId == '" + idInCluster + "'");

		logger.info("txtx filter");

		Collection<SessionData> col = null;
		try
		{
			col = (Collection<SessionData>) query.execute();
		} catch (Exception e)
		{
			logger.error("txtx asdf", e);
		}

		logger.info("txtx loaded session...");

		SessionData session = col.iterator().next();

		logger.info("txtx loaded session2222: " + session.getSessionId());

		try
		{
			if (session != null)
			{
				session.restoreSerialization();

				if (target == null)
				{
					target = new DistributedSession(session);
					logger.info("created a NEW session object");
				} else
				{
					target.updateSessionData(session);
					logger.info("Updated an old session object");
				}

			}
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		return target;
	}

	private void deleteSession(DistributedSession session)
	{
		init();

		Transaction tx = pm.currentTransaction();

		SessionData data = session.getSessionData();

		tx.begin();
		pm.deletePersistent(data);
		tx.commit();

		logger.info("txtx session deleted");
	}

	@Override
	protected void addSession(AbstractSessionManager.Session session)
	{
		if (session == null)
			return;

		DistributedSession disSession = (DistributedSession) session;

		synchronized (this)
		{
			logger.info("Put session: " + disSession.getClusterId());
			sessions.put(disSession.getClusterId(), disSession);
			try
			{
				disSession.willPassivate();
				storeSession(disSession);
				disSession.didActivate();
			} catch (Exception e)
			{
				logger.warn("Unable to store new session id=" + session.getId(), e);
			}
		}

		String clusterId = disSession.getClusterId();
	}

	@Override
	public Session getSession(String idInCluster)
	{
		logger.info("Get session: " + idInCluster);

		DistributedSession session = sessions.get(idInCluster);

		synchronized (this)
		{
			if (session == null)
			{
				logger.info("txtx loading session");
				session = loadSession(session, idInCluster);
				sessions.put(session.getClusterId(), session);
			} else
			{
				logger.warn("Reloading session!!");
				loadSession(session, idInCluster);
			}
		}

		return session;
	}

	@Override
	public Map getSessionMap()
	{
		return Collections.unmodifiableMap(sessions);
	}

	@Override
	public int getSessions()
	{
		int size = 0;
		synchronized (this)
		{
			size = sessions.size();
		}

		logger.debug("get sessions: " + size);

		return size;
	}

	@Override
	protected void invalidateSessions()
	{
		// Do nothing
	}

	protected void invalidateSession(String idInCluster)
	{
		synchronized (this)
		{
			DistributedSession session = sessions.get(idInCluster);
			if (session != null)
				session.invalidate();
		}
	}

	@Override
	protected Session newSession(HttpServletRequest request)
	{
		logger.debug("new session for request");
		return new DistributedSession(request);
	}

	public void doStart() throws Exception
	{
		sessions = new ConcurrentHashMap();
		super.doStart();
	}

	public void doStop() throws Exception
	{
		sessions.clear();
		sessions = null;
		super.doStop();
	}

	@Override
	protected void removeSession(String idInCluster)
	{
		synchronized (this)
		{
			try
			{
				DistributedSession session = sessions.remove(idInCluster);
				deleteSession(session);
			} catch (Exception e)
			{
				logger.warn("Problem deleting session id = " + idInCluster, e);
			}
		}
	}

	public void removeSession(AbstractSessionManager.Session session, boolean invalidate)
	{
		DistributedSession disSession = (DistributedSession) session;

		synchronized (super._sessionIdManager)
		{
			boolean removed = false;

			synchronized (this)
			{
				if (sessions.get(disSession.getClusterId()) != null)
				{
					removed = true;
					removeSession(disSession.getClusterId());
				}
			}

			if (removed)
			{
				super._sessionIdManager.removeSession(session);
				if (invalidate)
					super._sessionIdManager.invalidateAll(disSession.getClusterId());
			}
		}

		if (invalidate && super._sessionListeners != null)
		{
			HttpSessionEvent event = new HttpSessionEvent(session);
			for (int i = LazyList.size(super._sessionListeners); i-- > 0;)
				((HttpSessionListener) LazyList.get(_sessionListeners, i)).sessionDestroyed(event);
		}

		if (!invalidate)
		{
			disSession.willPassivate();
		}
	}

	protected void expire(List sessionIds)
	{
		if (isStopping() || isStopped())
			return;
	}
}

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
package org.prot.appserver.runtime.java;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.AbstractSessionManager;
import org.eclipse.jetty.util.LazyList;
import org.prot.appserver.runtime.java.data.SessionDao;

public class DistributedSessionManager extends AbstractSessionManager
{
	private static final Logger logger = Logger.getLogger(DistributedSessionManager.class);

	private ConcurrentHashMap<String, DistributedSession> sessions;

	private SessionDao sessionDao;

	@SuppressWarnings("serial")
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
		
		public String getSessionId()
		{
			return data.getSessionId(); 
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
					updateSession(this);
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

	private void storeSession(DistributedSession session)
	{
		sessionDao.saveSession(session.getSessionData());
	}

	private void updateSession(DistributedSession sessionData)
	{
		sessionDao.updateSession(sessionData.getSessionData());
	}

	private DistributedSession loadSession(DistributedSession target, String sessionId)
	{
		// Check if storage has the session
		boolean exists = sessionDao.exists(sessionId);
		if (exists == false)
			return null;

		// Is cached session stale
		boolean stale = true;
		if (target != null)
			stale = sessionDao.isStale(sessionId, target.getLastAccessedTime());

		// Cached session is ok - return
		if (!stale)
			return target;

		// Fetch the session from the storage
		SessionData loadedSession = sessionDao.loadSession(sessionId);

		// Update or create a session
		if (target != null)
		{
			target.updateSessionData(loadedSession);
		} else
		{
			target = new DistributedSession(loadedSession);
		}

		return target;
	}

	private void deleteSession(DistributedSession session)
	{
		sessionDao.deleteSession(session.getSessionData());
	}

	@Override
	protected void addSession(AbstractSessionManager.Session session)
	{
		// Do nothing if session is null
		if (session == null)
			return;

		// Make a cast to the corrent type
		DistributedSession disSession = (DistributedSession) session;

		synchronized (this)
		{
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
	}

	@Override
	public Session getSession(String idInCluster)
	{
		// Check if the session is in the cache
		DistributedSession session = sessions.get(idInCluster);

		synchronized (this)
		{
			loadSession(session, idInCluster);
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

		return size;
	}

	@Override
	protected void invalidateSessions()
	{
		logger.warn("invalidateSessions() does nothing");
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

	public void setSessionDao(SessionDao sessionDao)
	{
		this.sessionDao = sessionDao;
	}
}

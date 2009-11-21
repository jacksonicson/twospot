package org.prot.appserver.runtime.java;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.AbstractSessionIdManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.prot.appserver.runtime.java.data.SessionDao;

public class DistributedSessionIdManager extends AbstractSessionIdManager
{
	private SessionDao sessionDao;

	private Set<String> sessionIds = new HashSet<String>();

	public DistributedSessionIdManager(Server server)
	{
		super(server);
	}

	@Override
	public void addSession(HttpSession session)
	{
		// If the session is null do nothing
		if (session == null)
			return;

		synchronized (sessionIds)
		{
			String id = ((DistributedSessionManager.DistributedSession) session).getSessionId();
			sessionDao.addSessionId(id);
			sessionIds.add(id);
		}
	}

	@Override
	public String getClusterId(String nodeId)
	{
		int dot = nodeId.lastIndexOf('.');
		return (dot > 0) ? nodeId.substring(0, dot) : nodeId;
	}

	@Override
	public String getNodeId(String clusterId, HttpServletRequest request)
	{
		if (_workerName != null)
			return clusterId + '.' + _workerName;

		return clusterId;
	}

	@Override
	public boolean idInUse(String id)
	{
		if (id == null)
			return false;

		String clusterId = getClusterId(id);

		synchronized (sessionIds)
		{
			if (sessionIds.contains(id))
				return true;

			if (sessionDao.existsSessionId(id))
				return true;
		}

		return false;
	}

	@Override
	public void invalidateAll(String id)
	{
		// Delete it in the storage
		sessionDao.deleteSessionId(id);

		synchronized (sessionIds)
		{
			// Inform all contexts about the invalidation
			Handler[] contexts = _server.getChildHandlersByClass(ContextHandler.class);

			for (int i = 0; contexts != null && i < contexts.length; i++)
			{
				SessionManager manager = ((SessionHandler) ((ContextHandler) contexts[i])
						.getChildHandlerByClass(SessionHandler.class)).getSessionManager();

				if (manager instanceof DistributedSessionManager)
				{
					((DistributedSessionManager) manager).invalidateSession(id);
				}
			}
		}
	}

	@Override
	public void removeSession(HttpSession session)
	{
		if (session == null)
			return;

		removeSession(((DistributedSessionManager.DistributedSession) session).getSessionId());
	}

	public void removeSession(String id)
	{
		if (id == null)
			return;

		synchronized (sessionIds)
		{
			sessionIds.remove(id);
			sessionDao.deleteSessionId(id);
		}
	}

	public synchronized void setSessionDao(SessionDao sessionDao)
	{
		this.sessionDao = sessionDao;
	}
}

package org.prot.appserver.runtime.java;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.session.AbstractSessionManager;

public class DistributedSessionManager extends AbstractSessionManager implements SessionManager
{
	@Override
	protected void addSession(Session session)
	{
		System.out.println("Add Session"); 
	}

	@Override
	public Session getSession(String idInCluster)
	{
		System.out.println("Get Session: " + idInCluster); 
		return null;
	}

	@Override
	public Map getSessionMap()
	{
		System.out.println("Get session Map"); 
		return null;
	}

	@Override
	public int getSessions()
	{
		System.out.println("Get number of Session"); 
		return 0;
	}

	@Override
	protected void invalidateSessions()
	{
		System.out.println("Invalidate SEssion"); 
		
	}

	@Override
	protected Session newSession(HttpServletRequest request)
	{
		System.out.println("New Sesssion"); 
		return null;
	}

	@Override
	protected void removeSession(String idInCluster)
	{
		System.out.println("Remove session");
	}

}

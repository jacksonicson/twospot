package org.prot.appserver.runtime.java;

import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.session.SessionHandler;

public class DistributedSessionHandler extends SessionHandler
{
	public DistributedSessionHandler(SessionManager manager)
	{
		super(manager);
	}
}

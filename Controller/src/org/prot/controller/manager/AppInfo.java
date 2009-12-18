package org.prot.controller.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.jetty.continuation.Continuation;
import org.prot.util.ReservedAppIds;

public final class AppInfo
{
	private static final Logger logger = Logger.getLogger(AppInfo.class);

	// Maximum time until the AppServer is idle
	private static final int MAX_TIME_TO_IDLE = 1 * 20 * 1000;

	// AppId
	private final String appId;

	// Port under which the appserver runs
	private final int port;

	// Start time of this app
	private final long startTime = System.currentTimeMillis();
	
	// State of the appserver
	private AppState status = AppState.OFFLINE;

	// Timestamp which tells last usage
	private long lastUsed;

	// Is this a privileged application
	private final boolean privileged;

	// Token which is used to authenticate the process
	private final String processToken;

	// Continuations for requests which are waiting for this appserver
	private List<Continuation> continuations = new ArrayList<Continuation>();

	void touch()
	{
		this.lastUsed = System.currentTimeMillis();
	}

	boolean isIdle()
	{
		return (System.currentTimeMillis() - this.lastUsed) > MAX_TIME_TO_IDLE;
	}
	
	long getStartTime()
	{
		return this.startTime;
	}

	AppInfo(String appId, int port)
	{
		this.appId = appId;
		this.port = port;

		// Determine if this is a privileged application
		privileged = ReservedAppIds.isPrivilged(appId);

		// Generate a authentication token
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		long token = Math.abs(random.nextLong() | System.currentTimeMillis());
		processToken = "" + token;
	}

	public synchronized boolean addContinuation(Continuation continuation)
	{
		switch (status)
		{
		case ONLINE:
		case FAILED:
		case KILLED:
			return false;
		}

		continuations.add(continuation);

		return true;
	}

	public synchronized void resume()
	{
		for (Continuation continuation : continuations)
			continuation.resume();

		continuations.clear();
	}

	public String getAppId()
	{
		return appId;
	}

	public int getPort()
	{
		return port;
	}

	public  synchronized AppState getStatus()
	{
		return status;
	}

	public synchronized void setStatus(AppState status)
	{
		this.status = status;
	}

	public boolean isPrivileged()
	{
		return privileged;
	}

	public int hashCode()
	{
		return appId.hashCode();
	}

	public boolean equals(Object o)
	{
		if (!(o instanceof AppInfo))
			return false;

		AppInfo cmp = (AppInfo) o;
		return cmp.getAppId().equals(this.appId);
	}

	public String getProcessToken()
	{
		return processToken;
	}
}

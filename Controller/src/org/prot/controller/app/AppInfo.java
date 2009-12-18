package org.prot.controller.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.jetty.continuation.Continuation;
import org.prot.util.ReservedAppIds;

public final class AppInfo
{
	private static final Logger logger = Logger.getLogger(AppInfo.class);

	// AppId
	private final String appId;

	// Port under which the appserver is running
	private final int port;

	// Token which is used to authenticate the process (null if this App is not
	// privileged)
	private final String processToken;

	// Timestamp when this AppInfo was created
	private final long creationTime = System.currentTimeMillis();

	// Timestamp which tells last usage
	private long touch;

	// State of the appserver
	private AppState status = AppState.OFFLINE;

	// Reference to the Process of this app
	private final AppProcess appProcess = new AppProcess();

	// Reference to the Ping-Interface which is used to transport management
	// data
	private final AppManagement appManagement = new AppManagement();

	// Continuations for requests which are waiting for this appserver
	private List<Continuation> continuations = new ArrayList<Continuation>();

	AppInfo(String appId, int port)
	{
		this.appId = appId;
		this.port = port;

		// Generate a authentication token
		if (ReservedAppIds.isPrivilged(appId))
		{
			Random random = new Random();
			random.setSeed(System.currentTimeMillis());
			long token = Math.abs(random.nextLong() | System.currentTimeMillis());
			processToken = "" + token;
		} else
		{
			processToken = null;
		}
	}

	void touch()
	{
		this.touch = System.currentTimeMillis();
	}

	public long getTouch()
	{
		return this.touch;
	}

	public long getCreationTime()
	{
		return this.creationTime;
	}

	public synchronized boolean addContinuation(Continuation continuation)
	{
		// Check if a continuation could be set
		switch (status)
		{
		case ONLINE:
		case FAILED:
		case STALE:
		case KILLED:
			return false;
		}

		// Insert the continuation
		continuations.add(continuation);

		return true;
	}

	public synchronized void resumeContinuations()
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

	public AppState getStatus()
	{
		return status;
	}

	public synchronized void setStatus(AppState status)
	{
		// Check if this is a valid state change
		boolean check = this.status == status;
		switch (this.status)
		{
		case OFFLINE:
			check |= status == AppState.STARTING;
			break;
		case STALE:
			check |= status == AppState.STARTING;
			check |= status == AppState.KILLED;
			break;
		case STARTING:
			check |= status == AppState.ONLINE;
			check |= status == AppState.FAILED;
			break;
		case FAILED:
			check |= status == AppState.KILLED;
			break;
		case ONLINE:
			check |= status == AppState.KILLED;
			check |= status == AppState.STALE;
			break;
		case KILLED:
			check |= status == AppState.OFFLINE;
			break;
		default:
			logger.warn("Unknown AppServer state");
		}

		if (check)
			this.status = status;
		else
			logger.warn("Invalid sate change from " + this.status + " to " + status);
	}

	public boolean isPrivileged()
	{
		return processToken != null;
	}

	public AppProcess getAppProcess()
	{
		return appProcess;
	}

	public AppManagement getAppManagement()
	{
		return appManagement;
	}

	public String getProcessToken()
	{
		return processToken;
	}

	public boolean equals(Object o)
	{
		if (!(o instanceof AppInfo))
			return false;

		AppInfo cmp = (AppInfo) o;
		return cmp.getAppId().equals(this.appId);
	}

	public int hashCode()
	{
		return appId.hashCode();
	}

	public void dump()
	{
		logger.debug("Continuations: " + continuations.size());
	}
}

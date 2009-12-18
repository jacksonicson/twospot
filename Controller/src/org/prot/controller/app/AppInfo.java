package org.prot.controller.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.jetty.continuation.Continuation;
import org.prot.util.ReservedAppIds;

public final class AppInfo
{
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

	long getCreationTime()
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
		this.status = status;
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
}

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

	// Constants
	public static final String CONTINUATION_ATTRIBUTE_APPINFO = "AppInfo";

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

	// Number of active requests
	private int activeRequests;

	// Timestamp of the last state change
	private long stateChange = System.currentTimeMillis();

	// State of the appserver
	private AppState state = AppState.NEW;

	// Reference to the Process of this app
	private final AppProcess appProcess = new AppProcess();

	// Reference to the Ping-Interface which is used to transport management
	// data
	private final AppManagement appManagement = new AppManagement();

	// Continuations for requests which are waiting for this appserver
	private List<Continuation> continuations = new ArrayList<Continuation>();

	AppInfo(String appId, int port)
	{
		// Set appId and port
		this.appId = appId;
		this.port = port;

		// Generate a authentication token if this a privileged application
		if (ReservedAppIds.isPrivilged(appId))
		{
			Random random = new Random();
			random.setSeed(System.currentTimeMillis());
			long token = Math.abs(random.nextLong() | System.currentTimeMillis());
			processToken = "t" + token;
		} else
		{
			processToken = null;
		}
	}

	synchronized void startRequest()
	{
		this.activeRequests++;
	}

	synchronized public void stopRequest()
	{
		this.activeRequests--;
	}

	int getActiveRequests()
	{
		return this.activeRequests;
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

	synchronized boolean addContinuation(Continuation continuation)
	{
		// Check continuation (debug)
		if (!continuation.isInitial())
			logger.warn("Not using an initial continuation");

		// Check state
		switch (state)
		{
		case NEW:
		case STARTING:
			// Add this to the continuation
			continuation.setAttribute(CONTINUATION_ATTRIBUTE_APPINFO, this);

			// Insert the continuation
			continuations.add(continuation);
			return true;
		}

		return false;
	}

	synchronized void finishContinuations()
	{
		for (Continuation continuation : continuations)
			continuation.complete();

		continuations.clear();
	}

	synchronized void resumeContinuations()
	{
		for (Continuation continuation : continuations)
			continuation.resume();

		continuations.clear();
	}
	
	synchronized void completeContinuations()
	{
		for (Continuation continuation : continuations)
			continuation.complete();

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
		return state;
	}

	public synchronized void setState(AppState status)
	{
		// Check if this is a valid state change
		boolean check = this.state == status;
		boolean finishContinuations = false;
		switch (this.state)
		{
		case NEW:
			check |= status == AppState.STARTING;
			check |= status == AppState.DEPLOYED;
			break;

		case STARTING:
			check |= status == AppState.ONLINE;
			check |= status == AppState.DEPLOYED; // Always possible
			check |= status == AppState.KILLED; // If ProcessWorker fails
			break;

		case ONLINE:
			check |= status == AppState.BANNED;
			check |= status == AppState.DROPPED;
			check |= status == AppState.DEPLOYED;
			check |= status == AppState.KILLED; // Stale appserver
			break;

		case BANNED:
			check |= status == AppState.KILLED;
			finishContinuations |= true;
			break;

		case DEPLOYED:
			check |= status == AppState.KILLED;
			finishContinuations |= true;
			break;

		case DROPPED:
			check |= status == AppState.KILLED;
			finishContinuations |= true;
			break;

		case KILLED:
			check |= status == AppState.DEAD;
			finishContinuations |= true;
			break;

		case DEAD:
			finishContinuations |= true;
			break;
		}

		if (finishContinuations)
			finishContinuations();

		if (check)
		{
			this.stateChange = System.currentTimeMillis();
			this.state = status;
		} else
		{
			logger.warn("Invalid state change from " + this.state + " to " + status);
		}
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

	public long getStateTime()
	{
		return System.currentTimeMillis() - stateChange;
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

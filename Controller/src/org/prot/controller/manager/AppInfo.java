package org.prot.controller.manager;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.continuation.Continuation;

public class AppInfo
{
	// AppId
	private final String appId;

	// Port under which the appserver runs
	private final int port;

	// State of the appserver
	private AppState status = AppState.OFFLINE;

	// Continuations for requests which are waiting for this appserver
	private List<Continuation> continuations = new ArrayList<Continuation>();

	public AppInfo(String appId, int port)
	{
		this.appId = appId;
		this.port = port;
	}

	public synchronized void addContinuation(Continuation continuation)
	{
		this.continuations.add(continuation);
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

	public synchronized AppState getStatus()
	{
		return status;
	}

	public synchronized void setStatus(AppState status)
	{
		this.status = status;
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
}

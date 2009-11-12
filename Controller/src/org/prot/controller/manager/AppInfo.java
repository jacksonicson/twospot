package org.prot.controller.manager;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.continuation.Continuation;

public class AppInfo
{
	private String appId; 
	
	private int port;
	
	private int managementPort; 
	
	private AppState status = AppState.OFFLINE; 
	
	private long lastInteraction; 
	
	private List<Continuation> continuations = new ArrayList<Continuation>();
	
	public synchronized void addContinuation(Continuation continuation) {
		this.continuations.add(continuation); 
	}
	
	public synchronized void resume()
	{
		for(Continuation continuation : continuations)
			continuation.resume(); 
	}
	
	public String getAppId()
	{
		return appId;
	}

	public void setAppId(String appId)
	{
		this.appId = appId;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public synchronized AppState getStatus()
	{
		return status;
	}


	public synchronized void setStatus(AppState status)
	{
		this.status = status;
	}
	

	public int hashCode() {
		return appId.hashCode(); 
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof AppInfo))
			return false; 
		
		AppInfo cmp = (AppInfo)o;
		return cmp.getAppId().equals(this.appId); 
	}

	public int getManagementPort()
	{
		return managementPort;
	}

	public void setManagementPort(int managementPort)
	{
		this.managementPort = managementPort;
	}

	public long getLastInteraction()
	{
		return lastInteraction;
	}

	public void setLastInteraction(long lastInteraction)
	{
		this.lastInteraction = lastInteraction;
	}
}

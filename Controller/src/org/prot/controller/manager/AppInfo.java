package org.prot.controller.manager;

public class AppInfo
{
	private String appId; 
	
	private int port;
	
	private AppState status = AppState.OFFLINE; 
	
	
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

	public AppState getStatus()
	{
		return status;
	}


	public void setStatus(AppState status)
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
}

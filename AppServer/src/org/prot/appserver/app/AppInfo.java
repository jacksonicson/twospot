package org.prot.appserver.app;


public class AppInfo
{
	private String appId; 
	
	private byte[] warFile;
	

	public String getAppId()
	{
		return appId;
	}

	public void setAppId(String appId)
	{
		this.appId = appId;
	}

	public byte[] getWarFile()
	{
		return warFile;
	}

	public void setWarFile(byte[] warFile)
	{
		this.warFile = warFile;
	}
}

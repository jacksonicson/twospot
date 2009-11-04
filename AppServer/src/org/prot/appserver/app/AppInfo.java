package org.prot.appserver.app;

import org.prot.appserver.AppRuntime;

public class AppInfo
{
	private String appId;

	private AppRuntime runtime;

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

	public AppRuntime getRuntime()
	{
		return runtime;
	}

	public void setRuntime(AppRuntime runtime)
	{
		this.runtime = runtime;
	}
}

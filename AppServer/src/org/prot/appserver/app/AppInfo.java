package org.prot.appserver.app;

import java.util.HashSet;
import java.util.Set;

import org.prot.appserver.AppRuntime;

public class AppInfo
{
	private String appId;

	private AppRuntime runtime;

	private byte[] warFile;

	private Set<WebConfiguration> webConfigurations = new HashSet<WebConfiguration>();

	public void addWebConfiguration(WebConfiguration webConfiguration)
	{
		this.webConfigurations.add(webConfiguration);
	}

	public Set<WebConfiguration> getWebConfigurations()
	{
		return this.webConfigurations;
	}

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

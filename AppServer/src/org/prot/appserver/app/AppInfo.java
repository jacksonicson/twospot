package org.prot.appserver.app;

import java.util.HashSet;
import java.util.Set;

import org.prot.appserver.AppRuntimeType;

public class AppInfo
{
	private String appId;

	private AppRuntimeType runtime;

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

	public AppRuntimeType getRuntime()
	{
		return runtime;
	}

	public void setRuntime(AppRuntimeType runtime)
	{
		this.runtime = runtime;
	}
}

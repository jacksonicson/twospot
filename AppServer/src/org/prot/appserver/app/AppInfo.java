package org.prot.appserver.app;

import java.util.HashSet;
import java.util.Set;

import org.prot.appserver.AppRuntimeType;

public class AppInfo
{
	private byte[] warFile;
	
	private String appId;

	private String runtime; 

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

	public String getRuntime()
	{
		return runtime;
	}

	public void setRuntime(String runtime)
	{
		this.runtime = runtime;
	}
}

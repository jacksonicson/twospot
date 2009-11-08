package org.prot.appserver.app;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AppInfo
{
	private byte[] warFile;

	private String appId;

	private String runtime;

	private RuntimeConfiguration runtimeConfiguration; 

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

	public RuntimeConfiguration getRuntimeConfiguration()
	{
		return runtimeConfiguration;
	}

	public void setRuntimeConfiguration(RuntimeConfiguration runtimeConfiguration)
	{
		this.runtimeConfiguration = runtimeConfiguration;
	}
}

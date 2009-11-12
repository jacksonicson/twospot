package org.prot.appserver.app;

public class AppInfo
{
	// Binary WAR-file (downloaded from the fileserver)
	private byte[] warFile;

	// AppId
	private String appId;

	// Name of the runtime
	private String runtime;

	// Object which contains runtime specific settings
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

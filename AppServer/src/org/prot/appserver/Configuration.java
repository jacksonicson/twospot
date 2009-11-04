package org.prot.appserver;

public class Configuration
{

	private static Configuration configuration;

	// Application identifier
	private String appId;

	// Port which is used to commmunicate with the Controller
	private int controlPort;

	// Port which is used by the AppServer to publish the app
	private int appServerPort;

	public static Configuration getInstance()
	{
		if (Configuration.configuration == null)
		{
			Configuration.configuration = new Configuration();
		}

		return Configuration.configuration;
	}

	public String getAppId()
	{
		return appId;
	}

	public void setAppId(String appId)
	{
		this.appId = appId;
	}

	public int getControlPort()
	{
		return controlPort;
	}

	public void setControlPort(int controlPort)
	{
		this.controlPort = controlPort;
	}

	public int getAppServerPort()
	{
		return appServerPort;
	}

	public void setAppServerPort(int appServerPort)
	{
		this.appServerPort = appServerPort;
	}
}

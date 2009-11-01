package org.prot.appserver;

public class Configuration {
	
	private static Configuration configuration; 
	
	private String appId;
	private int controlPort;
	private int appServerPort; 

	public static Configuration getInstance() {
		if(Configuration.configuration == null) {
			Configuration.configuration = new Configuration(); 
		}
		
		return Configuration.configuration;
	}
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public int getControlPort() {
		return controlPort;
	}

	public void setControlPort(int controlPort) {
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

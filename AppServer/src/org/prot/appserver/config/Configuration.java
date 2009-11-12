package org.prot.appserver.config;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.prot.appserver.app.AppInfo;

public class Configuration
{
	private static Logger logger = Logger.getLogger(Configuration.class);
	
	// Singleton
	private static Configuration configuration;

	// Enable stdout
	private boolean enableStdOut; 
	
	// Application identifier
	private String appId;

	// Port which is used to commmunicate with the Controller
	private int controlPort;

	// Port which is used by the AppServer to publish the app
	private int appServerPort;
	
	// Directory which is used to extract the application data (configuration)
	private String workingDirectory;
	
	// Directory which holds the current application data
	private String appDirectory;
	
	// Directories with the python libs
	private String pythonLibs; 
	private String djangoLibs;
	
	// AppInfo
	private AppInfo appInfo; 
	
	public static Configuration getInstance()
	{
		if (Configuration.configuration == null)
		{
			Configuration.configuration = new Configuration();
			loadConfiguration(Configuration.configuration); 
		}

		return Configuration.configuration;
	}
	
	private static void loadConfiguration(Configuration configuration)
	{
		Properties props = new Properties(); 
		try
		{
			props.load(Configuration.class.getResourceAsStream("/etc/config.properties"));
			configuration.workingDirectory = props.getProperty("working.dir");
			configuration.pythonLibs = props.getProperty("python.lib");
			configuration.djangoLibs = props.getProperty("python.lib.site-packages");
		} catch (IOException e)
		{
			logger.error(e); 
		}
		
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

	public String getWorkingDirectory()
	{
		return workingDirectory;
	}

	public void setWorkingDirectory(String workingDirectory)
	{
		this.workingDirectory = workingDirectory;
	}

	public String getAppDirectory()
	{
		return appDirectory;
	}

	public void setAppDirectory(String appDirectory)
	{
		this.appDirectory = appDirectory;
	}

	public String getPythonLibs()
	{
		return pythonLibs;
	}

	public void setPythonLibs(String pythonLibs)
	{
		this.pythonLibs = pythonLibs;
	}

	public String getDjangoLibs()
	{
		return djangoLibs;
	}

	public void setDjangoLibs(String djangoLibs)
	{
		this.djangoLibs = djangoLibs;
	}

	public AppInfo getAppInfo()
	{
		return appInfo;
	}

	public void setAppInfo(AppInfo appInfo)
	{
		this.appInfo = appInfo;
	}

	public boolean isEnableStdOut()
	{
		return enableStdOut;
	}

	public void setEnableStdOut(boolean enableStdOut)
	{
		this.enableStdOut = enableStdOut;
	}
}

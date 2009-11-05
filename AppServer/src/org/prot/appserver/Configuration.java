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
	
	// Directory which is used to extract the application data (configuration)
	private String workingDirectory = "C:/temp";
	
	// Directory which holds the current application data
	private String appDirectory;
	
	// Directories with the python libs
	private String pythonLibs = "C:/jython2.5.1/Lib"; 
	private String djangoLibs = "C:/jython2.5.1/Lib/site-packages"; 
	
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
}

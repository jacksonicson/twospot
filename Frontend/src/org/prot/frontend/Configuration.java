package org.prot.frontend;

public class Configuration
{
	private static Configuration configuration = null;

	private String managerAddress = null; 
	
	public static Configuration get()
	{
		if (Configuration.configuration == null)
			Configuration.configuration = new Configuration();

		return configuration;
	}

	public static Configuration getConfiguration()
	{
		return configuration;
	}

	public static void setConfiguration(Configuration configuration)
	{
		Configuration.configuration = configuration;
	}

	public String getManagerAddress()
	{
		return managerAddress;
	}

	public void setManagerAddress(String managerAddress)
	{
		this.managerAddress = managerAddress;
	}
}

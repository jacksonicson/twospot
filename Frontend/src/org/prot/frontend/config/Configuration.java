package org.prot.frontend.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Configuration
{
	private static final Logger logger = Logger.getLogger(Configuration.class);

	private static Configuration configuration;

	private Properties properties = new Properties();

	private String managerAddress = null;

	public static Configuration getConfiguration()
	{
		if (configuration == null)
			configuration = new Configuration();

		return configuration;
	}

	public Configuration()
	{
		InputStream in = this.getClass().getResourceAsStream("/etc/config.properties");
		try
		{
			properties.load(in);

		} catch (IOException e)
		{
			logger.error("Could not load configuration", e);
			System.exit(1);
		} catch (NumberFormatException e)
		{
			logger.error("Could not parse the configuration", e);
			System.exit(1);
		}
	}

	public Properties getProperties()
	{
		return properties;
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

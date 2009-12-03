package org.prot.manager.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Configuration
{
	private static final Logger logger = Logger.getLogger(Configuration.class);

	private static Configuration configuration;

	private Properties properties = new Properties();

	// Port under which the RMI registry of the Controller runs
	private int rmiControllerPort = -1;

	public static Configuration getConfiguration()
	{
		if (configuration == null)
			configuration = new Configuration();

		return configuration;
	}

	Configuration()
	{
		InputStream in = this.getClass().getResourceAsStream("/etc/config.properties");
		try
		{
			properties.load(in);

			rmiControllerPort = Integer.parseInt(properties.getProperty("rmi.controller.registry.port"));

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

	public int getRmiControllerPort()
	{
		return rmiControllerPort;
	}
}

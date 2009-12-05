package org.prot.controller.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Configuration
{
	private static final Logger logger = Logger.getLogger(Configuration.class);

	// Singleton instance
	private static Configuration configuration;

	// Configuration
	private Properties properties = new Properties();

	// Domain plus port under which the platform is running
	private String platformDomain;

	// Port on which the Controller listens
	private int controllerPort = -1;

	public static Configuration getConfiguration()
	{
		if (configuration == null)
		{
			configuration = new Configuration();
		}

		return configuration;
	}

	public Configuration()
	{
		InputStream in = this.getClass().getResourceAsStream("/etc/config.properties");
		try
		{
			properties.load(in);

			this.controllerPort = Integer.parseInt(properties.getProperty("http.controller.port"));
			this.platformDomain = properties.getProperty("platform.domain");

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

	public int getControllerPort()
	{
		return controllerPort;
	}

	public String getPlatformDomain()
	{
		return platformDomain;
	}
}

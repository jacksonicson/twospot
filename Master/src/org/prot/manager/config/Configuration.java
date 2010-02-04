package org.prot.manager.config;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Configuration
{
	private static final Logger logger = Logger.getLogger(Configuration.class);

	private static Configuration configuration;

	private Properties properties = new Properties();

	// Port under which the RMI registry of the Controller runs
	private int rmiControllerPort = -1;

	// Port under which the UDP server for management data is listening
	private int masterDatagramPort = -1;

	// Load-Balancer
	private double slbInstanceCpuLimit;
	private double slbInstanceOverloadLimit;
	private double slbMinIdleCpu;
	private int slbTotalCpuUnits;
	private int slbGuaranteedCpuUnits;

	public static Configuration getConfiguration()
	{
		if (configuration == null)
			configuration = new Configuration();

		return configuration;
	}

	Configuration()
	{
		try
		{
			// Load the global configuration file
			properties.load(this.getClass().getResourceAsStream("/etc/config.properties"));

			// Management-Data
			this.rmiControllerPort = Integer.parseInt(properties.getProperty("rmi.controller.registry.port"));
			this.masterDatagramPort = Integer.parseInt(properties.getProperty("master.datagramPort"));

			// Load balancer configuration settings
			this.slbInstanceCpuLimit = Double.parseDouble(properties.getProperty("slb.instance.cpuLimit"));
			this.slbInstanceOverloadLimit = Double.parseDouble(properties
					.getProperty("slb.instance.overloadLimit"));
			this.slbMinIdleCpu = Double.parseDouble(properties.getProperty("slb.controller.minIdleTime"));
			this.slbTotalCpuUnits = Integer.parseInt(properties.getProperty("slb.controller.totalCpuUnits"));
			this.slbGuaranteedCpuUnits = Integer.parseInt(properties
					.getProperty("slb.controller.guranteedCpuUnits"));

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

	public int getMasterDatagramPort()
	{
		return masterDatagramPort;
	}

	public double getSlbInstanceCpuLimit()
	{
		return slbInstanceCpuLimit;
	}

	public double getSlbInstanceOverloadLimit()
	{
		return slbInstanceOverloadLimit;
	}

	public double getSlbMinIdleCpu()
	{
		return slbMinIdleCpu;
	}

	public int getSlbTotalCpuUnits()
	{
		return slbTotalCpuUnits;
	}

	public int getSlbGuaranteedCpuUnits()
	{
		return slbGuaranteedCpuUnits;
	}
}

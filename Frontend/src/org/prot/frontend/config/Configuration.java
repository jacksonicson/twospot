/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
package org.prot.frontend.config;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Configuration
{
	private static final Logger logger = Logger.getLogger(Configuration.class);

	private static Configuration configuration;

	private Properties properties = new Properties();

	private String managerAddress = null;

	private int masterRmiPort = -1;

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
			properties.load(this.getClass().getResourceAsStream("/etc/config.properties"));

			this.masterRmiPort = Integer.parseInt(properties.getProperty("rmi.registry.port"));

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

	public int getMasterRmiPort()
	{
		return masterRmiPort;
	}
}

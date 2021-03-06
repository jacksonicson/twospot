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
package org.prot.controller.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.prot.util.net.AddressExtractor;

public class Configuration {
	private static final Logger logger = Logger.getLogger(Configuration.class);

	// Singleton instance
	private static Configuration configuration;

	// UID of this controller
	private final String UID;

	// Address
	private String address;

	// Configuration
	private Properties properties = new Properties();

	// UDP port under which the management datagram server is running
	private int controllerDatagramPort = -1;

	// UDP port under whic hthe management datagram server of the Master is
	// running
	private int masterDatagramPort = -1;

	// Address of the Master
	private String masterAddress = null;

	// Domain plus port under which the platform is running
	private String platformDomain;

	// Port on which the Controller listens
	private int controllerPort = -1;

	// Prefix which ist used when building the classpath for the AppServer
	private String classpathPrefix = "";

	// Contains an additional classpath which is added to the classpath file
	private String additionalClasspath = "";

	// Connection URL for the fileserver
	private String fileServerURL;

	// VM options which are used when starting the AppServer
	private String vmOptions;

	// TwoSpotV8 appserver configuration
	private String twoSpotV8Bin = "";

	public static Configuration getConfiguration() {
		if (configuration == null) {
			configuration = new Configuration();
		}

		return configuration;
	}

	private final InetAddress getInetAddress(String networkInterface) throws SocketException {
		return AddressExtractor.getInetAddress(networkInterface, false);
	}

	public Configuration() {
		// UID
		this.UID = java.util.UUID.randomUUID().toString();

		// Read the configuration files
		InputStream utilIn = this.getClass().getResourceAsStream("/etc/config.properties");
		InputStream controllerIn = this.getClass().getResourceAsStream("/etc/controller.properties");
		try {
			properties.load(utilIn);
			properties.load(controllerIn);

			this.controllerPort = Integer.parseInt(properties.getProperty("http.controller.port"));

			this.platformDomain = properties.getProperty("platform.domain");

			this.classpathPrefix = properties.getProperty("appserver.classpath.prefix");

			this.additionalClasspath = properties.getProperty("appserver.classpath.additional");

			this.fileServerURL = properties.getProperty("fileserver.url");

			this.vmOptions = properties.getProperty("appserver.vm.options");

			this.address = getInetAddress(properties.getProperty("zk.controller.networkInterface"))
					.getHostAddress();

			this.controllerDatagramPort = Integer.parseInt(properties.getProperty("controller.datagramPort"));

			this.masterDatagramPort = Integer.parseInt(properties.getProperty("master.datagramPort"));

			this.twoSpotV8Bin = properties.getProperty("twospotv8.bin");

		} catch (SocketException e) {
			logger.error("Could not parse the configuration", e);
			System.exit(1);
		} catch (IOException e) {
			logger.error("Could not load configuration", e);
			System.exit(1);
		} catch (NumberFormatException e) {
			logger.error("Could not parse the configuration", e);
			System.exit(1);
		} catch (NullPointerException e) {
			logger.error("Could not parse the configuration", e);
			System.exit(1);
		}
	}

	public Properties getProperties() {
		return properties;
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public int getControllerPort() {
		return controllerPort;
	}

	public String getPlatformDomain() {
		return platformDomain;
	}

	public String getClasspathPrefix() {
		return classpathPrefix;
	}

	public String getAdditionalClasspath() {
		return additionalClasspath;
	}

	public String getFileServerURL() {
		return fileServerURL;
	}

	public String getVmOptions() {
		return vmOptions;
	}

	public String getUID() {
		return UID;
	}

	public String getAddress() {
		return address;
	}

	public int getControllerDatagramPort() {
		return controllerDatagramPort;
	}

	public int getMasterDatagramPort() {
		return masterDatagramPort;
	}

	public String getMasterAddress() {
		return masterAddress;
	}

	public void setMasterAddress(String masterAddress) {
		this.masterAddress = masterAddress;
	}

	public String getTwoSpotV8Bin() {
		return twoSpotV8Bin;
	}

	public void setTwoSpotV8Bin(String twoSpotV8Bin) {
		this.twoSpotV8Bin = twoSpotV8Bin;
	}
}

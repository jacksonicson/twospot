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
package org.prot.appserver.config;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.prot.appserver.app.AppInfo;

public class Configuration {
	private static Logger logger = Logger.getLogger(Configuration.class);

	// Singleton
	private static Configuration configuration;

	// Merged properties from all configuration files
	private final static Properties properties = new Properties();

	// In which mode is the server running
	private ServerMode serverMode = ServerMode.SERVER;

	// Port under wich the local RMI registry is running
	private int controllerRmiRegistryPort = -1;

	// Port under which the UDP server of the Controller is nunning
	private int controllerDatagramPort = -1;

	// Enable stdout
	private boolean enableStdOut = false;

	// Shutdown if Controller is not available
	private boolean requiresController = true;

	// Application identifier
	private String appId;

	// Is the app executed by this server a privileged app
	private boolean privileged = false;

	// If this is a privileged app it must authenticate with the controller
	private String authenticationToken;

	// Port which is used by the AppServer to publish the app
	private int appServerPort;

	// Directory which is used to extract the application data (configuration)
	private String workingDirectory;

	// Directory which holds the current application data
	private String appDirectory;

	// Directory which is used as scratch dir for the web application
	private String appScratchDir;

	// Maximum request time
	private long dosPreventionTime = 40000;

	// Directories with the python libs
	private String pythonLibs;
	private String djangoLibs;

	// AppInfo
	private AppInfo appInfo;

	/**
	 * Singlethon get the instance of the Configuration
	 * 
	 * @return intance of the Configuration
	 */
	public static Configuration getInstance() {
		if (Configuration.configuration == null) {
			// Create a new Configuration
			Configuration.configuration = new Configuration();

			// Init the Configuration
			initConfiguration(Configuration.configuration);
		}

		return Configuration.configuration;
	}

	private static void initConfiguration(Configuration configuration) {
		try {
			// Load all propertie files
			properties.load(Configuration.class.getResourceAsStream("/etc/config.properties"));
			properties.load(Configuration.class.getResourceAsStream("/etc/appServer.properties"));

			// General configuration settings
			configuration.serverMode = ServerMode.valueOf(properties.getProperty("appServer.mode"));

			configuration.pythonLibs = properties.getProperty("python.lib");
			configuration.djangoLibs = properties.getProperty("python.lib.site-packages");

			configuration.appScratchDir = properties.getProperty("appServer.scratchdir");

			configuration.dosPreventionTime = Long.parseLong(properties
					.getProperty("appServer.security.DOSPrevention.time"));

			configuration.workingDirectory = properties.getProperty("appserver.workDir");

			configuration.controllerDatagramPort = Integer.parseInt(properties
					.getProperty("controller.datagramPort"));

			switch (configuration.serverMode) {
			case SERVER:
				configuration.controllerRmiRegistryPort = Integer.parseInt(properties
						.getProperty("rmi.controller.registry.port"));
				break;

			case DEVELOPMENT:
				break;
			}

		} catch (IOException e) {
			logger.error("Could not load the configuration properties", e);
			System.exit(1);
		} catch (NumberFormatException e) {
			logger.error("Could not parse the configuration file");
			System.exit(1);
		}
	}

	/**
	 * Executed after all propertie files and command line arguments have been
	 * set
	 */
	void postInitialize() {
		Configuration config = Configuration.configuration;

		// Get the working directory
		String workingDir = config.getWorkingDirectory();

		// Replace all backslashes
		workingDir.replace('\\', '/');

		// Remove all trailing slahes
		while (workingDir.endsWith("/"))
			workingDir = workingDir.substring(0, workingDir.length() - 1);

		switch (configuration.serverMode) {
		case SERVER:
			config.setAppDirectory(workingDir);
			config.setAppScratchDir(config.getAppScratchDir() + "/" + config.getAppServerPort());
			break;

		case DEVELOPMENT:
			config.setAppDirectory(workingDir + "/" + config.getAppId());
			config.setAppScratchDir(config.getAppScratchDir() + "/" + config.getAppServerPort());
			break;
		}

		logger.info("Configured AppDirectory: " + config.getAppDirectory());
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public int getAppServerPort() {
		return appServerPort;
	}

	public void setAppServerPort(int appServerPort) {
		this.appServerPort = appServerPort;
	}

	public String getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(String workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public String getAppDirectory() {
		return appDirectory;
	}

	void setAppDirectory(String appDirectory) {
		this.appDirectory = appDirectory;
	}

	public String getPythonLibs() {
		return pythonLibs;
	}

	public void setPythonLibs(String pythonLibs) {
		this.pythonLibs = pythonLibs;
	}

	public String getDjangoLibs() {
		return djangoLibs;
	}

	public void setDjangoLibs(String djangoLibs) {
		this.djangoLibs = djangoLibs;
	}

	public AppInfo getAppInfo() {
		return appInfo;
	}

	public void setAppInfo(AppInfo appInfo) {
		this.appInfo = appInfo;
	}

	public boolean isEnableStdOut() {
		return enableStdOut;
	}

	public void setEnableStdOut(boolean enableStdOut) {
		this.enableStdOut = enableStdOut;
	}

	public boolean isRequiresController() {
		return requiresController;
	}

	public void setRequiresController(boolean requiresController) {
		this.requiresController = requiresController;
	}

	public boolean isPrivileged() {
		return privileged;
	}

	public void setPrivileged(boolean privileged) {
		this.privileged = privileged;
	}

	public String getAuthenticationToken() {
		return authenticationToken;
	}

	public void setAuthenticationToken(String authenticationToken) {
		logger.debug("Using authentication token: " + authenticationToken);
		this.authenticationToken = authenticationToken;
	}

	public String getAppScratchDir() {
		return appScratchDir;
	}

	void setAppScratchDir(String appScratchDir) {
		this.appScratchDir = appScratchDir;
	}

	public int getRmiRegistryPort() {
		return controllerRmiRegistryPort;
	}

	public ServerMode getServerMode() {
		return serverMode;
	}

	public void setServerMode(ServerMode serverMode) {
		this.serverMode = serverMode;
	}

	public long getDosPreventionTime() {
		return dosPreventionTime;
	}

	public static Properties getProperties() {
		return properties;
	}

	public int getControllerDatagramPort() {
		return controllerDatagramPort;
	}
}

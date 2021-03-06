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
package org.prot.controller.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;
import org.prot.controller.app.launcher.AppLauncher;
import org.prot.controller.app.launcher.LauncherRegistry;
import org.prot.controller.app.lifecycle.appconfig.AppConfigurer;
import org.prot.controller.app.lifecycle.appfetch.HttpAppFetcher;
import org.prot.controller.app.lifecycle.extract.AppExtractor;
import org.prot.controller.app.lifecycle.extract.WarExtractor;
import org.prot.util.io.Directory;

class ProcessHandler {
	// Logger
	private static final Logger logger = Logger.getLogger(ProcessHandler.class);

	// Info sent by appserver
	private static final String SERVER_ONLINE = "server online";
	private static final String SERVER_FAILED = "server failed";

	// Launcher registry
	private LauncherRegistry launcherRegistry;

	private void stopAndClean(AppInfo appInfo, AppProcess process) {

		String baseDir = "C:/temp";
		String appDir = baseDir + "/" + appInfo.getPort();
		File fAppDir = new File(appDir);
		if (fAppDir.exists())
			Directory.deleteFolder(new File(appDir));

		if (process.getProcess() == null)
			return;

		try {
			process.getProcess().exitValue();
		} catch (IllegalThreadStateException e) {
			logger.debug("Process is still running");
			try {
				process.getProcess().destroy();
				process.setProcess(null);
			} catch (Exception killerr) {
				logger.error("Could not stop process: ", killerr);
			}
		}
	}

	void stop(AppInfo appInfo, AppProcess appProcess) {
		logger.debug("Stopping AppServer...");
		stopAndClean(appInfo, appProcess);
	}

	private byte[] loadApp(String appId) {
		HttpAppFetcher fetcher = new HttpAppFetcher();
		fetcher.setUrl("http://localhost:5050/");
		byte[] archive = fetcher.fetchApp(appId);
		return archive;
	}

	private final boolean extractApp(String appId, String destPath, byte[] archive) {
		AppExtractor extractor = new WarExtractor();

		try {
			extractor.extract(archive, destPath, appId);
		} catch (IOException e) {
			logger.error("Error while extracting application package", e);
			return false;
		}

		return true;
	}

	boolean execute(AppInfo appInfo, AppProcess appProcess) {

		// Kill the old process if exists
		stopAndClean(appInfo, appProcess);

		logger.debug("Downloading application archive...");
		byte[] archive = loadApp(appInfo.getAppId());
		if (archive == null) {
			logger.error("Could not download application archive: " + appInfo.getAppId());
			return false;
		}

		logger.debug("Extracting appplication archive...");
		String baseDir = "C:/temp";
		String appDir = baseDir + "/" + appInfo.getPort();

		boolean extracted = extractApp(appInfo.getAppId(), appDir, archive);
		if (extracted == false) {
			return false;
		}

		logger.debug("Reading configuration...");
		AppConfigurer configurer = new AppConfigurer();
		String runtime;
		try {
			runtime = configurer.configure(appDir);
		} catch (ConfigurationException e1) {
			logger.error("Could not read application configuration");
			return false;
		}

		logger.debug("Starting AppServer...");

		AppLauncher launcher = launcherRegistry.getLauncher(runtime);
		if (launcher == null) {
			logger.error("Unknown launcher for " + runtime);
			return false;
		}

		// Creating the launch command
		List<String> command = launcher.createCommand(appInfo, appDir);

		// configure the process
		long time = System.currentTimeMillis();
		ProcessBuilder procBuilder = new ProcessBuilder();
		procBuilder.command(command);
		procBuilder.redirectErrorStream(true);

		try {
			// Start the process
			logger.debug("Starting process...");
			Process process = procBuilder.start();
			appProcess.setProcess(process);

			// Wait until the Server running
			logger.debug("Waiting for AppServer...");
			waitForAppServer(process);

			// Update the AppServer state
			time = System.currentTimeMillis() - time;
			logger.debug("AppServer is ONLINE (started in " + time + " ms)");
			return true;

		} catch (IOException e) {
			// Log the error
			logger.error("Could not start a new server process (AppId: " + appInfo.getAppId() + " Command: "
					+ command.toString() + ")", e);

			stopAndClean(appInfo, appProcess);
			return false;
		}
	}

	private void waitForAppServer(Process process) throws IOException {
		// create IO streams
		BufferedReader stdInStream = null;

		try {
			stdInStream = new BufferedReader(new InputStreamReader(process.getInputStream()));

			// Read the input stream until SERVER_ONLINE sequence is found
			String line = "";

			while ((line = stdInStream.readLine()) != null) {
				logger.debug("appserver> " + line);

				if (line.equalsIgnoreCase(SERVER_ONLINE)) {
					logger.info("AppServer is ONLINE");
					return;
				} else if (line.equalsIgnoreCase(SERVER_FAILED)) {
					logger.info("AppServer FAILED");
					throw new IOException("AppServer FAILED");
				}
			}

			// AppServer is not online - we did not recive SERVER_ONLINE or
			// SERVER_FAILED
			logger.info("AppServer is NOT ONLINE");
			throw new IOException("AppServer FAILED");

		} catch (IOException e) {
			// Log the error
			logger.error("Error while starting AppServer");

			// Rethrow this exception
			throw e;

		} finally {
			try {
				// Close the input stream
				if (stdInStream != null)
					stdInStream.close();
			} catch (IOException e) {
				logger.trace(e);
			}
		}
	}

	public void setLauncherRegistry(LauncherRegistry launcherRegistry) {
		this.launcherRegistry = launcherRegistry;
	}
}

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
package org.prot.appserver.runtime.winstone;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.prot.appserver.app.AppInfo;
import org.prot.appserver.config.Configuration;
import org.prot.appserver.management.RuntimeManagement;
import org.prot.appserver.runtime.AppRuntime;
import org.prot.appserver.runtime.java.JavaConfiguration;

import winstone.Launcher;

public class WinstoneRuntime implements AppRuntime {
	private static final Logger logger = Logger.getLogger(WinstoneRuntime.class);

	private static final String IDENTIFIER = "WINSTONE";

	private WinstoneAppManagement appManagement = new WinstoneAppManagement();

	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}

	@Override
	public void launch(AppInfo appInfo) throws Exception {
		logger.debug("Launching java runtime (winstone)");

		// Configure server port
		int port = Configuration.getInstance().getAppServerPort();
		logger.debug("Configuring server port: " + port);

		// Get the configuration
		JavaConfiguration runtimeConfig = (JavaConfiguration) appInfo.getRuntimeConfiguration();
		Configuration configuration = Configuration.getInstance();

		Map<String, String> args = new HashMap<String, String>();
		args.put("webroot", configuration.getAppDirectory());
		args.put("httpPort", "" + port);
		args.put("handlerCountStartup", "" + 1);
		args.put("useJasper", ""+true);

		Launcher.initLogger(args);
		new Launcher(args);

		logger.info("Server is online now");
	}

	@Override
	public void loadConfiguration(AppInfo appInfo, Map<?, ?> yaml) {

	}

	@Override
	public RuntimeManagement getManagement() {
		return appManagement;
	}
}

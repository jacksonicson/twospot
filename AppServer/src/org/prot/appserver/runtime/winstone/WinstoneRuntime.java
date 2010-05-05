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
	private static final Logger logger = Logger
			.getLogger(WinstoneRuntime.class);

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
		JavaConfiguration runtimeConfig = (JavaConfiguration) appInfo
				.getRuntimeConfiguration();
		Configuration configuration = Configuration.getInstance();

		Map<String, String> args = new HashMap<String, String>();
		args.put("webroot", configuration.getAppDirectory());
		args.put("httpPort", ""+port);

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

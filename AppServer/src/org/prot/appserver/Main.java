package org.prot.appserver;

import java.security.Policy;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.prot.app.security.HardPolicy;
import org.prot.appserver.config.AppConfigurer;
import org.prot.appserver.config.ArgumentParser;
import org.prot.appserver.config.Configuration;
import org.prot.appserver.config.ServerMode;
import org.prot.appserver.management.AppServerManager;
import org.prot.appserver.runtime.AppRuntime;
import org.prot.appserver.runtime.RuntimeRegistry;
import org.prot.appserver.runtime.java.JavaRuntime;

import com.googlecode.protobuf.socketrpc.SocketRpcChannel;
import com.googlecode.protobuf.socketrpc.SocketRpcController;

public class Main {
	public Main() {

		long time = System.currentTimeMillis();

		// Configure logger
		PropertyConfigurator.configure(Main.class.getResource("/etc/log4j.properties"));
		final Logger logger = Logger.getLogger(Main.class);
		logger.info("Starting AppServer...");

		logger.error("Time: " + (System.currentTimeMillis() - time));

		// Load basic configuration settings
		Configuration.getInstance();

		// Start the IODirector
		IODirector ioDirector = new IODirector();
		if (Configuration.getInstance().isEnableStdOut())
			ioDirector.enableStd();

		// Start the security manager (only in server mode)
		if (Configuration.getInstance().getServerMode() == ServerMode.SERVER) {
			HardPolicy policy = new HardPolicy();
			policy.refresh();
			Policy.setPolicy(policy);
			// System.setSecurityManager(new SecurityManager());
		}

		// Log all startup arguments
		ArgumentParser.dump();

		// Configure HBase namespace (TODO: Make this more generic)
		// StorageHelper.setAppId(Configuration.getInstance().getAppId());

		logger.error("Time: " + (System.currentTimeMillis() - time));

		// Determine which configuration to use

		// String configurationFile = null;
		// switch (Configuration.getInstance().getServerMode()) {
		// case DEVELOPMENT:
		// configurationFile = "/etc/spring_development.xml";
		// break;
		// case SERVER:
		// configurationFile = "/etc/spring.xml";
		// break;
		// }

		// Load the beans
		// logger.info("Using spring configuration: " + configurationFile);
		// XmlBeanFactory factory = new XmlBeanFactory(new
		// ClassPathResource(configurationFile, getClass()));

		// Postprocess the factory
		// Properties props = Configuration.getInstance().getProperties();
		// props.setProperty("appId", Configuration.getInstance().getAppId());

		/*
		 * PropertyPlaceholderConfigurer configurer = new
		 * PropertyPlaceholderConfigurer(); configurer.setProperties(props);
		 */

		// PythonRuntime rtPython = new PythonRuntime();
		JavaRuntime rtJava = new JavaRuntime();
		// WinstoneRuntime rtWinstone = new WinstoneRuntime();

		RuntimeRegistry rtRegistry = new RuntimeRegistry();
		List<AppRuntime> runtimes = new ArrayList<AppRuntime>();
		// runtimes.add(rtPython);
		runtimes.add(rtJava);
		// runtimes.add(rtWinstone);
		rtRegistry.setRuntimes(runtimes);

		AppServerManager appManager = new AppServerManager();

		AppConfigurer appConfigurer = new AppConfigurer();
		appConfigurer.setRuntimeRegistry(rtRegistry);

		ServerLifecycle lifecycle = new ServerLifecycle();
		lifecycle.setRuntimeRegistry(rtRegistry);
		lifecycle.setAppConfigurer(appConfigurer);
		lifecycle.setAppExtractor(null);
		lifecycle.setAppFetcher(null);
		lifecycle.setAppManager(appManager);

		lifecycle.start();

		// Start the Monitor
		if (Configuration.getInstance().isRequiresController())
			new Monitor();

		// configurer.postProcessBeanFactory(factory);

		// Initialize the server

		// Get the beans
		// factory.getBean("Lifecycle");

		// Create channel
		

		// If the AppServer is running in Development mode - do some more
		// initialization
		if (Configuration.getInstance().getServerMode() == ServerMode.DEVELOPMENT)
			initDev();
	}

	private void initDev() {
		// Do nothing here
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		// Parse command line arguments
		ArgumentParser.parseArguments(args);
		System.out.println("TIME: " + (System.currentTimeMillis() - time));

		// Launch
		new Main();
	}
}

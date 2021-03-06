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
package org.prot.appserver.runtime.java;

import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.prot.app.security.DosPrevention;
import org.prot.app.security.DosPreventionHandler;
import org.prot.appserver.app.AppInfo;
import org.prot.appserver.config.Configuration;
import org.prot.appserver.management.RuntimeManagement;
import org.prot.appserver.runtime.AppRuntime;

import ort.prot.util.server.CountingRequestLog;

public class JavaRuntime implements AppRuntime {
	private static final Logger logger = Logger.getLogger(JavaRuntime.class);

	private static final String IDENTIFIER = "JAVA";

	private JettyAppManagement jettyAppManagement;

	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}

	@Override
	public void launch(AppInfo appInfo) throws Exception {
		logger.debug("Launching java runtime");

		Connector connector = new SelectChannelConnector();
		connector.setHost("0.0.0.0");
		connector.setPort(9090);
		connector.setStatsOn(false);
		connector.setMaxIdleTime(5000);

		QueuedThreadPool tp = new QueuedThreadPool();
		tp.setMinThreads(1);
		tp.setMaxThreads(20);

		HashSessionIdManager sessionManager = new HashSessionIdManager();
		sessionManager.setRandom(new Random());
		sessionManager.setWorkerName("work1");

		ContextHandlerCollection collection = new ContextHandlerCollection();

		DosPrevention dos = new DosPrevention();
		CountingRequestLog crl = new CountingRequestLog();

		DosPreventionHandler dosHandler = new DosPreventionHandler();
		dosHandler.setHandler(collection);
		dosHandler.setDosPrevention(dos);

		RequestLogHandler crlHandler = new RequestLogHandler();
		crlHandler.setRequestLog(crl);

		HandlerCollection handlers = new HandlerCollection();
		handlers.setHandlers(new Handler[] { dosHandler, crlHandler });

		AppDeployer deployer = new AppDeployer();
		deployer.setContexts(collection);

		Server server = new Server();
		server.setThreadPool(tp);
		server.setConnectors(new Connector[] { connector });
		server.setHandler(handlers);
		server.setSessionIdManager(sessionManager);

		JettyAppManagement jettyAppManagement = new JettyAppManagement();
		jettyAppManagement.setConnector(connector);
		jettyAppManagement.setCountingRequestLog(crl);

		// XmlBeanFactory factory = new XmlBeanFactory(new
		// ClassPathResource("/etc/spring_java.xml", getClass()));

		// Configure server port
		int port = Configuration.getInstance().getAppServerPort();
		logger.debug("Configuring server port: " + port);
		// Connector connector = (Connector) factory.getBean("Connector");
		connector.setPort(port);

		// Load and init AppDeployer
		logger.debug("Initialize the AppDeployer");
		// AppDeployer deployer = (AppDeployer) factory.getBean("AppDeployer");
		deployer.setAppInfo(appInfo);

		// Start the server
		logger.debug("Creating server");
		// Server server = (Server) factory.getBean("Server");

		// Create the management components
		logger.debug("Creating management");
		// jettyAppManagement = (JettyAppManagement)
		// factory.getBean("JettyAppManagement");

		// Activate the slf4j logging facade (which is bound to log4j)
		logger.debug("Configuring slf4j logging");
		org.eclipse.jetty.util.log.Log.setLog(new Slf4jLog());

		// Add the deployer to the server
		server.addBean(deployer);
		try {
			logger.debug("Starting jetty");
			long time = System.currentTimeMillis();
			server.start();
			logger.info("Jetty started in " + (System.currentTimeMillis() - time));
		} catch (Exception e) {
			logger.error("Could not start the jetty server", e);
			throw e;
		}
	}

	@Override
	public void loadConfiguration(AppInfo appInfo, Map<?, ?> yaml) {
		// Create a specific java configuration
		JavaConfiguration configuration = new JavaConfiguration();
		appInfo.setRuntimeConfiguration(configuration);

		// Default - do not use distributed sessions
		configuration.setUseDistributedSessions(false);

		// Check if the distributed sessions are configured
		Object distSession = yaml.get("distSession");
		if (distSession != null) {
			// Check if the setting is a boolean value
			if (distSession instanceof Boolean) {
				try {
					configuration.setUseDistributedSessions((Boolean) distSession);
				} catch (NumberFormatException e) {
					logger.error("Could not parse configuration");
					System.exit(1);
				}
			} else {
				logger.warn("app.yaml setting distSession must be boolean");
			}
		}

		logger.debug("Using distributed sessions: " + configuration.isUseDistributedSessions());
	}

	@Override
	public RuntimeManagement getManagement() {
		return jettyAppManagement;
	}
}

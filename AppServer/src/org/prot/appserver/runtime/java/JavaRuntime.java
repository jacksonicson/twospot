package org.prot.appserver.runtime.java;

import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.prot.appserver.app.AppInfo;
import org.prot.appserver.config.Configuration;
import org.prot.appserver.runtime.AppRuntime;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class JavaRuntime implements AppRuntime
{
	private static final Logger logger = Logger.getLogger(JavaRuntime.class);

	private static final String IDENTIFIER = "JAVA";

	@Override
	public String getIdentifier()
	{
		return IDENTIFIER;
	}

	@Override
	public void launch(AppInfo appInfo) throws Exception
	{
		logger.debug("Launching java runtime");

		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("/etc/spring_java.xml", getClass()));

		// Configure server port
		SelectChannelConnector connector = (SelectChannelConnector) factory.getBean("Connector");
		connector.setPort(Configuration.getInstance().getAppServerPort());

		// Load and init AppDeployer
		AppDeployer deployer = (AppDeployer) factory.getBean("AppDeployer");
		deployer.setAppInfo(appInfo);

		// Start server
		Server server = (Server) factory.getBean("Server");
		server.addBean(deployer);
		try
		{
			server.start();
		} catch (Exception e)
		{
			logger.error("Could not start the jetty server", e);
			throw e;
		}
	}

	@Override
	public void loadConfiguration(AppInfo appInfo, Map<?, ?> yaml)
	{
		// Create a specific java configuration
		JavaConfiguration configuration = new JavaConfiguration();
		appInfo.setRuntimeConfiguration(configuration);

		// Default - do not use distributed sessions
		configuration.setUseDistributedSessions(false);

		// Check if the distributed sessions are configured
		Object distSession = yaml.get("distSession");
		if (distSession != null)
		{
			// Check if the setting is a boolean value
			if (distSession instanceof Boolean)
			{
				try
				{
					configuration.setUseDistributedSessions((Boolean) distSession);
				} catch (NumberFormatException e)
				{
					logger.error("Could not parse configuration");
					System.exit(1);
				}
			} else
			{
				logger.warn("app.yaml setting distSession must be boolean");
			}
		}

		logger.debug("Using distributed sessions: " + configuration.isUseDistributedSessions());
	}
}

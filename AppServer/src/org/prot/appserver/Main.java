package org.prot.appserver;

import java.security.Policy;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.prot.app.security.HardPolicy;
import org.prot.appserver.config.ArgumentParser;
import org.prot.appserver.config.Configuration;
import org.prot.appserver.config.ServerMode;
import org.prot.jdo.storage.StorageHelper;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class Main
{
	public Main()
	{
		// Configure logger
		DOMConfigurator.configure(Main.class.getResource("/etc/log4j.xml"));
		final Logger logger = Logger.getLogger(Main.class);
		logger.info("Starting AppServer...");

		// Load basic configuration settings
		Configuration.getInstance();
		
		// Start the IODirector
		IODirector ioDirector = new IODirector();
		if (Configuration.getInstance().isEnableStdOut())
			ioDirector.enableStd();

		// Start the security manager (only in server mode)
		if (Configuration.getInstance().getServerMode() == ServerMode.SERVER)
		{
			HardPolicy policy = new HardPolicy();
			policy.refresh();
			Policy.setPolicy(policy);
			System.setSecurityManager(new SecurityManager());
		}

		// Log all startup arguments
		ArgumentParser.dump();

		// Configure HBase namespace (TODO: Make this more generic)
		StorageHelper.setAppId(Configuration.getInstance().getAppId());

		// Start the Monitor
		if (Configuration.getInstance().isRequiresController())
			new Monitor();

		// Determine which configuration to use
		String configurationFile = null;
		switch (Configuration.getInstance().getServerMode())
		{
		case DEVELOPMENT:
			configurationFile = "/etc/spring_development.xml";
			break;
		case SERVER:
			configurationFile = "/etc/spring.xml";
			break;
		}

		// Load the beans
		logger.info("Using spring configuration: " + configurationFile);
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource(configurationFile, getClass()));

		// Postprocess the factory
		Properties props = Configuration.getInstance().getProperties();
		props.setProperty("appId", Configuration.getInstance().getAppId());
		PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
		configurer.setProperties(props);
		configurer.postProcessBeanFactory(factory);

		// Get the beans
		factory.getBean("ManagementExporter");
		factory.getBean("Lifecycle");

		// If the AppServer is running in Development mode - do some more
		// initialization
		if (Configuration.getInstance().getServerMode() == ServerMode.DEVELOPMENT)
			initDev();
	}

	private void initDev()
	{
		// Do nothing here
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// Parse command line arguments
		ArgumentParser.parseArguments(args);

		// Launch
		new Main();
	}
}

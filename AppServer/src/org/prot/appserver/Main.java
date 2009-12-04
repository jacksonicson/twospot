package org.prot.appserver;

import java.security.Policy;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.datanucleus.store.hbase.HBaseUtils;
import org.prot.app.security.HardPolicy;
import org.prot.appserver.config.ArgumentParser;
import org.prot.appserver.config.Configuration;
import org.prot.appserver.config.ServerMode;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class Main
{
	public Main()
	{
		// Load basic configuration settings
		Configuration.getInstance();

		// Start the security manager (only in server mode)
		if (Configuration.getInstance().getServerMode() == ServerMode.SERVER)
		{
			HardPolicy policy = new HardPolicy();
			policy.refresh();
			Policy.setPolicy(policy);
			System.setSecurityManager(new SecurityManager());
		}

		// Start the IODirector
		IODirector ioDirector = new IODirector();
		if (Configuration.getInstance().isEnableStdOut())
			ioDirector.enableStd();

		// Configure logger
		DOMConfigurator.configure(Main.class.getResource("/etc/log4j.xml"));
		final Logger logger = Logger.getLogger(Main.class);

		// Log all startup arguments
		ArgumentParser.dump();

		// Configure HBase (TODO: Make this more generic)
		HBaseUtils.setNamespace(Configuration.getInstance().getAppId() + "."
				+ HBaseUtils.NAMESPACE_USER_TAGBLES);

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
		PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
		configurer.setProperties(Configuration.getProperties());
		configurer.postProcessBeanFactory(factory);

		// Get the beans
		factory.getBean("ManagementExporter");
		factory.getBean("Lifecycle");
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

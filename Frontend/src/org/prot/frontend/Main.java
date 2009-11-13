package org.prot.frontend;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.jetty.server.Server;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class Main
{
	private static final Logger logger = Logger.getLogger(Main.class);

	public Main()
	{
		// Configure logger
		DOMConfigurator.configure(Main.class.getResource("/etc/log4j.xml"));

		// Start spring IOC container
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("/etc/spring.xml", getClass()));

		// Postprocess the factory
		PropertyPlaceholderConfigurer conf = (PropertyPlaceholderConfigurer)factory.getBean("PropertyConfigurer");
		conf.postProcessBeanFactory(factory);
		
		// Start the server
		Server server = (Server) factory.getBean("Server");
		try
		{
			server.start();
		} catch (Exception e)
		{
			logger.fatal("could not start server", e);
			System.exit(1); 
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		new Main();
	}
}

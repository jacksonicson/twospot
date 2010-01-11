package org.prot.controller;

import org.apache.log4j.xml.DOMConfigurator;
import org.prot.controller.config.Configuration;
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

		// Configure HBase (TODO: Make this more generic)
		StorageHelper.setAppId("twospot");

		// Start spring ioc container
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("/etc/spring.xml", getClass()));

		// Postprocess the factory
		PropertyPlaceholderConfigurer conf = (PropertyPlaceholderConfigurer) factory
				.getBean("PropertyConfigurer");
		conf.setProperties(Configuration.getConfiguration().getProperties());
		conf.postProcessBeanFactory(factory);

		// ZooKeeper
		factory.getBean("ManagementService");

		// Start the RMI-Services
		factory.getBean("DeployServiceExporter");
		factory.getBean("UserServiceExporter");
		factory.getBean("DbServiceExporter");
		factory.getBean("LogServiceExporter");

		// Start Management
		factory.getBean("UdpListener");

		Controller controller = (Controller) factory.getBean("Controller");
		controller.start();
	}

	public static void main(String arg[])
	{
		new Main();
	}
}

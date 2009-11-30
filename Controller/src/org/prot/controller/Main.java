package org.prot.controller;

import org.apache.log4j.xml.DOMConfigurator;
import org.prot.controller.management.ControllerManagement;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class Main
{
	public Main()
	{
		// Configure logger
		DOMConfigurator.configure(Main.class.getResource("/etc/log4j.xml"));

		// Start spring ioc container
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("/etc/spring.xml", getClass()));

		// Postprocess the factory
		PropertyPlaceholderConfigurer conf = (PropertyPlaceholderConfigurer) factory
				.getBean("PropertyConfigurer");
		conf.postProcessBeanFactory(factory);

		// ZooKeeper
		factory.getBean("ManagementService");

		// Request-Timer
		factory.getBean("RequestManager");

		// Start the RMI-Services
		factory.getBean("ControllerServiceExporter");
		factory.getBean("DeployServiceExporter");
		factory.getBean("UserServiceExporter");
		
		// Start JMX
		factory.getBean("JmxExporter");
		factory.getBean("ServerConnector");
		

		Controller controller = (Controller) factory.getBean("Controller");
		controller.start();
	}

	public static void main(String arg[])
	{
		new Main();
	}
}

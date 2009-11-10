package org.prot.controller;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class Main
{
	public Main()
	{
		// Configure logger
		DOMConfigurator.configure(Main.class.getResource("/etc/log4j/controller.xml"));

		// Start spring ioc container
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("/etc/spring/spring.xml",
				getClass()));

		factory.getBean("test");
		Controller controller = (Controller) factory.getBean("Controller");
		controller.start();
	}

	public static void main(String arg[])
	{
		new Main();
	}
}

package org.prot.appserver;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class Main
{

	public Main()
	{
		// Configure logger
		DOMConfigurator.configure(Main.class.getResource("/etc/log4j.xml"));

		// Create beans
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("/etc/spring.xml", getClass()));

		factory.getBean("Management");
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

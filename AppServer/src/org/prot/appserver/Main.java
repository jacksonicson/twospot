package org.prot.appserver;

import java.security.Policy;

import org.apache.log4j.xml.DOMConfigurator;
import org.prot.app.services.security.HardPolicy;
import org.prot.appserver.config.Configuration;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class Main
{
	public Main()
	{
		// Start the security manager
		// System.setSecurityManager(new SecurityManager());
		Policy.setPolicy(new HardPolicy());
		System.setSecurityManager(new SecurityManager());

		// Start the IODirector
		IODirector ioDirector = new IODirector();
		if (Configuration.getInstance().isEnableStdOut())
			ioDirector.enableStd();

		// Configure logger
		DOMConfigurator.configure(Main.class.getResource("/etc/log4j.xml"));

		// Start the Monitor
		if (Configuration.getInstance().isRequiresController())
			new Monitor();

		// Create beans
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("/etc/spring.xml", getClass()));
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

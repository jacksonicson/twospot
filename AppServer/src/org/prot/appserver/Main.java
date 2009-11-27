package org.prot.appserver;

import java.net.MalformedURLException;
import java.security.Policy;
import java.util.ArrayList;
import java.util.List;

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
		HardPolicy policy = new HardPolicy();
		policy.refresh(); 
		try
		{
			List<String> appDirs = new ArrayList<String>();
			appDirs.add("C:/temp/-");
			appDirs.add("D:/work/mscWolke/trunk/dev/Libs/-");
			policy.activateApplication(appDirs);
			
			List<String> serverDirs = new ArrayList<String>();
			serverDirs.add("D:/work/mscWolke/-");
			
			policy.activateServer(serverDirs);
			
		} catch (MalformedURLException e)
		{
			System.err.println("Could not activate policies");
			System.exit(1);
		}
		
		Policy.setPolicy(policy);
		System.setSecurityManager(new SecurityManager());

		// Start the IODirector
		IODirector ioDirector = new IODirector();
		if (Configuration.getInstance().isEnableStdOut())
			ioDirector.enableStd();

		// Configure logger
		DOMConfigurator.configure(Main.class.getResource("/etc/log4j.xml"));
		
		// Log all startup arguments
		ArgumentParser.dump();

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

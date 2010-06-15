/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
package org.prot.frontend;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.jetty.server.Server;
import org.prot.frontend.config.Configuration;
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
		conf.setProperties(Configuration.getConfiguration().getProperties());
		conf.postProcessBeanFactory(factory);
		
		// Load the beans
		factory.getBean("ManagementService"); 
		
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

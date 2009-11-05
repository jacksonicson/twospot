package org.prot.controller;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class Main
{
	public Main()
	{

		// start spring ioc container
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("/etc/spring/spring.xml",
				getClass()));

		// start the controller
		Controller controller = (Controller) factory.getBean("Controller");
		controller.start();
	}

	public static void main(String arg[])
	{
		new Main();
	}
}

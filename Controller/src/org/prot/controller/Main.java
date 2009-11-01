package org.prot.controller;

import java.util.logging.Logger;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class Main {

	private static final Logger logger = Logger.getLogger(Main.class.getName());

	public Main() {

		// start spring ioc container
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("/etc/spring/spring.xml",
				getClass()));
		
		// start the controller
		Controller controller = (Controller)factory.getBean("Controller");
		controller.start(); 
	}
	
	public static void main(String arg[]) {
		new Main();
	}
}

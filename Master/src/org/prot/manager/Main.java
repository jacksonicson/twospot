package org.prot.manager;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
	public Main() {
		// Configure the logger
		DOMConfigurator.configure(Main.class.getResource("/etc/log4j.xml"));

		// Start spring IOC container
		new ClassPathXmlApplicationContext("/etc/spring.xml");
	}

	public static void main(String arg[]) {
		new Main();
	}
}

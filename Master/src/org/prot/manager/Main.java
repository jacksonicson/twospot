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

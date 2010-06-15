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
package org.prot.controller;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;

public class Controller
{
	public static final boolean DEBUG = true;
	
	private static final Logger logger = Logger.getLogger(Controller.class);

	private Server server;

	public void setServer(Server server)
	{
		this.server = server;
	}

	public void start()
	{
		try
		{
			logger.info("Starting Controller");
			server.start();
		} catch (Exception e)
		{
			logger.error("Could not start the Controller", e);
			System.exit(1);
		}
	}

}

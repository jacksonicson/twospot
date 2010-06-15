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
package org.prot.app.services.db;

import org.apache.log4j.Logger;
import org.prot.app.services.PrivilegedServiceException;
import org.prot.appserver.config.Configuration;

public class DbBrowserServiceFactory {
	private static final Logger logger = Logger.getLogger(DbBrowserServiceFactory.class);

	private static final String CONTROLLER_ADDRESS = "localhost";

	private static DbBrowserService service;

	private static final int getRmiPort() {
		return Configuration.getInstance().getRmiRegistryPort();
	}

	private static DbBrowserService createDbBrowserService() {
		return new DbBrowserService();
	}

	private static DbBrowserService createMockDbBrowserService() {
		throw new PrivilegedServiceException();
	}

	public static final DbBrowserService getDbBrowserService() {
		if (service == null) {
			switch (Configuration.getInstance().getServerMode()) {
			case DEVELOPMENT:
				service = createMockDbBrowserService();
				break;
			case SERVER:
				service = createDbBrowserService();
				break;
			}
		}

		return service;
	}
}

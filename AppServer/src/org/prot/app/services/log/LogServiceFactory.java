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
package org.prot.app.services.log;

import org.apache.log4j.Logger;
import org.prot.appserver.config.Configuration;

public class LogServiceFactory {
	private static final Logger logger = Logger.getLogger(LogServiceFactory.class);

	private static final String CONTROLLER_ADDRESS = "localhost";

	private static LogService logService;

	private static final int getRmiPort() {
		return Configuration.getInstance().getRmiRegistryPort();
	}

	private static LogService createLogService() {
		return new LogServiceImpl();
	}

	private static LogService createMockLogService() {
		return new LogServiceMock();
	}

	public static LogService getLogService() {
		if (logService == null) {
			switch (Configuration.getInstance().getServerMode()) {
			case DEVELOPMENT:
				logService = createMockLogService();
				break;
			case SERVER:
				logService = createLogService();
				break;
			}
		}

		return logService;
	}
}

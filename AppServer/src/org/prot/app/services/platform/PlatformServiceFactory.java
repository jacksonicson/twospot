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
package org.prot.app.services.platform;

import org.apache.log4j.Logger;
import org.prot.appserver.config.Configuration;

public final class PlatformServiceFactory {
	private static final Logger logger = Logger.getLogger(PlatformServiceFactory.class);

	private static final String CONTROLLER_ADDRESS = "localhost";

	private static PlatformService platformService;

	private static final int getRmiPort() {
		return Configuration.getInstance().getRmiRegistryPort();
	}

	private static PlatformService createPlatformService() {
		return new PlatformService();
	}

	private static PlatformService createMockPlatformService() {
		return null;
	}

	public static PlatformService getPlatformService() {
		if (platformService == null) {
			switch (Configuration.getInstance().getServerMode()) {
			case DEVELOPMENT:
				platformService = createMockPlatformService();
				break;
			case SERVER:
				platformService = createPlatformService();
				break;
			}
		}

		return platformService;
	}
}

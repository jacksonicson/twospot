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

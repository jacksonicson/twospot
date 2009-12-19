package org.prot.controller.stats;

import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.app.AppInfo;
import org.prot.controller.app.AppState;
import org.prot.controller.zookeeper.ManagementService;

public class AlmostIdleProcessor implements BalancingProcessor
{
	private static final Logger logger = Logger.getLogger(AlmostIdleProcessor.class);

	private ManagementService management;

	@Override
	public void run(Set<AppInfo> appInfos)
	{
		logger.debug("Almost idle processor");

		long current = System.currentTimeMillis();
		for (AppInfo appInfo : appInfos)
		{
			double rps = appInfo.getAppManagement().getAppRequestStats().getRps();
			long time = current - appInfo.getCreationTime();
			if (rps < 30d && time > 20000)
			{
				logger.debug("Almost idle AppServer found - trying to shut down");

				// Check if this Controller is the last Controller serving the
				// AppServer
				boolean check = management.tryShutdown(appInfo.getAppId());

				logger.debug("Check: " + check);

				// If a shutdown is possible
				if (check)
				{
					logger.debug("Killing AppServer because of low load and multiple servings");
					// Everythin is ok - this Controller is not the last one
					// serving the AppServer
					appInfo.setStatus(AppState.KILLED);
				}
			}
		}
	}

	public void setManagement(ManagementService management)
	{
		this.management = management;
	}
}

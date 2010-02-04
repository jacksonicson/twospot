package org.prot.controller.stats.processors;

import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.app.AppInfo;
import org.prot.controller.app.AppState;
import org.prot.controller.config.Configuration;
import org.prot.controller.zookeeper.SynchronizationService;

public class AlmostIdleProcessor implements BalancingProcessor
{
	private static final Logger logger = Logger.getLogger(AlmostIdleProcessor.class);

	private int MIN_RUNTIME = Integer.MAX_VALUE;

	private SynchronizationService management;

	public AlmostIdleProcessor()
	{
		Configuration config = Configuration.getConfiguration();
		try
		{
			MIN_RUNTIME = Integer.parseInt(config.getProperty("balance.almostIdleProcessor.minRunime"));
		} catch (NumberFormatException e)
		{
			logger.fatal("Configuration failed", e);
			System.exit(1);
		}
	}

	@Override
	public void run(Set<AppInfo> appInfos)
	{
		for (AppInfo appInfo : appInfos)
		{
			// Check the state
			if (appInfo.getStatus() != AppState.ONLINE)
				continue;

			// Check if management data is available
			if (appInfo.getAppManagement().getAppServer() == null)
				continue;

			// Check if the minimum runtime
			long runtime = System.currentTimeMillis() - appInfo.getCreationTime();
			if (runtime < MIN_RUNTIME)
				continue;

			// Check the AppServer load
			if (appInfo.getAppManagement().getAppServer().getRps() < 5)
			{
				// Check if this Controller is the last Controller serving the
				// AppServer
				boolean check = management.tryStop(appInfo.getAppId());
				logger.debug("Trying to shutdown almost idle: " + appInfo.getAppId() + " - " + check);

				// If a shutdown is possible
				if (check)
				{
					logger.debug("Dropping : " + appInfo.getAppId());
					appInfo.setState(AppState.DROPPED);
				}
			}
		}
	}

	public void setManagement(SynchronizationService management)
	{
		this.management = management;
	}
}

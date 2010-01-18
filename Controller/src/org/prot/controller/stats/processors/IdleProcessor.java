package org.prot.controller.stats.processors;

import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.app.AppInfo;
import org.prot.controller.app.AppState;
import org.prot.controller.config.Configuration;

public class IdleProcessor implements BalancingProcessor
{
	private static final Logger logger = Logger.getLogger(IdleProcessor.class);

	private static int IDLE_THREASHOLD = Integer.MAX_VALUE;

	public IdleProcessor()
	{
		Configuration config = Configuration.getConfiguration();
		try
		{
			IDLE_THREASHOLD = Integer.parseInt(config.getProperty("balance.idleProcessor.idleTime"));
		} catch (NumberFormatException e)
		{
			logger.fatal("Configuration failed", e);
			System.exit(1);
		}
	}

	@Override
	public void run(Set<AppInfo> appInfos)
	{
		long currentTime = System.currentTimeMillis();

		// Iterate over all AppServers
		for (AppInfo appInfo : appInfos)
		{
			// Check if the AppServer is online
			if (appInfo.getStatus() != AppState.ONLINE)
				continue;

			// Check if there are management data for the AppServer
			if (appInfo.getAppManagement().getAppServer() == null)
				continue;

			// Check when the AppServr has been used the last time
			long time = appInfo.getTouch();
			time = currentTime - time;

			if (time > IDLE_THREASHOLD)
			{
				logger.debug("Killing IDLE AppServer: " + appInfo.getAppId());
//				appInfo.setState(AppState.KILLED);
			}
		}
	}
}

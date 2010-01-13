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
	private double LOW_RPS = Double.MAX_VALUE;

	private SynchronizationService management;

	public AlmostIdleProcessor()
	{
		Configuration config = Configuration.getConfiguration();
		try
		{
			MIN_RUNTIME = Integer.parseInt(config.getProperty("balance.almostIdleProcessor.minRunime"));
			LOW_RPS = Double.parseDouble(config.getProperty("balance.almostIdleProcessor.lowRps"));
		} catch (NumberFormatException e)
		{
			logger.fatal("Configuration failed", e);
			System.exit(1);
		}
	}

	@Override
	public void run(Set<AppInfo> appInfos)
	{
		long current = System.currentTimeMillis();
		for (AppInfo appInfo : appInfos)
		{
			if (appInfo.getStatus() != AppState.ONLINE)
				continue;
			if (appInfo.getAppManagement().getAppServer() == null)
				continue;

			double rps = appInfo.getAppManagement().getAppServer().getRps();
			long time = current - appInfo.getCreationTime();

			// TODO: < 50 % der Threads!
			if (rps < LOW_RPS && time > MIN_RUNTIME)
			{
				// Check if this Controller is the last Controller serving the
				// AppServer
				boolean check = management.tryStop(appInfo.getAppId());
				logger.debug("Trying to shutdown almost idle: " + appInfo.getAppId() + " - " + check);

				// If a shutdown is possible
				if (check)
				{
					logger.debug("Shutting down: " + appInfo.getAppId());

					// Everythin is ok - this Controller is not the last one
					// serving the AppServer
					appInfo.setState(AppState.BANNED);
				}
			}
		}
	}

	public void setManagement(SynchronizationService management)
	{
		this.management = management;
	}
}

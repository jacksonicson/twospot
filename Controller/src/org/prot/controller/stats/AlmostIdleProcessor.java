package org.prot.controller.stats;

import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.app.AppInfo;
import org.prot.controller.app.AppState;
import org.prot.controller.config.Configuration;
import org.prot.controller.zookeeper.ManagementService;

public class AlmostIdleProcessor implements BalancingProcessor
{
	private static final Logger logger = Logger.getLogger(AlmostIdleProcessor.class);

	private int MIN_RUNTIME = Integer.MAX_VALUE;
	private double LOW_RPS = Double.MAX_VALUE;

	private ManagementService management;

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
			double rps = appInfo.getAppManagement().getAppRequestStats().getRps();
			long time = current - appInfo.getCreationTime();
			if (rps < LOW_RPS && time > MIN_RUNTIME)
			{
				// Check if this Controller is the last Controller serving the
				// AppServer
				boolean check = management.tryShutdown(appInfo.getAppId());

				// If a shutdown is possible
				if (check)
				{
					logger.debug("Shutting down: " + appInfo.getAppId());

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

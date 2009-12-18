package org.prot.controller.stats;

import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.app.AppInfo;
import org.prot.controller.app.AppState;

public class IdleProcessor implements BalancingProcessor
{
	private static final Logger logger = Logger.getLogger(IdleProcessor.class);

	private static final int IDLE_THREASHOLD = 1 * 20 * 1000;

	@Override
	public void run(Set<AppInfo> appInfos)
	{
		logger.debug("Idle processor");

		long currentTime = System.currentTimeMillis();

		for (AppInfo appInfo : appInfos)
		{
			long time = appInfo.getTouch();
			time = currentTime - time;
			if (time > IDLE_THREASHOLD)
			{
				logger.debug("Idle AppServer detected: " + appInfo.getAppId());
				appInfo.setStatus(AppState.KILLED);
			}
		}
	}
}

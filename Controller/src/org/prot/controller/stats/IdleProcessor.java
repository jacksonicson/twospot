package org.prot.controller.stats;

import java.util.Set;

import org.prot.controller.app.AppInfo;
import org.prot.controller.app.AppState;

public class IdleProcessor implements BalancingProcessor
{
	private static final int IDLE_THREASHOLD = 1 * 20 * 1000;

	@Override
	public void test(Set<AppInfo> appInfos)
	{
		long currentTime = System.currentTimeMillis();

		for (AppInfo appInfo : appInfos)
		{
			long time = appInfo.getTouch();
			time = currentTime - time;
			if (time > IDLE_THREASHOLD)
				appInfo.setStatus(AppState.KILLED);
		}
	}
}

package org.prot.controller.stats;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.prot.controller.app.AppInfo;
import org.prot.controller.app.AppState;

public class LowRequestsProcessor implements BalancingProcessor
{
	@Override
	public void test(Set<AppInfo> appInfos)
	{
		// Check if Controller is under load
		// If yes - kill the AppServer with the average load
		// If no - do nothing
		
//		List<AppInfo> sortedInfos = new ArrayList<AppInfo>();
//		
//		long currentTime = System.currentTimeMillis();
//
//		for (AppInfo appInfo : appInfos)
//		{
//			long time = appInfo.getTouch();
//			time = currentTime - time;
//			if (time > IDLE_THREASHOLD)
//				appInfo.setStatus(AppState.KILLED);
//		}
	}
}

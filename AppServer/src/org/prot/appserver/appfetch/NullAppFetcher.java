package org.prot.appserver.appfetch;

import org.apache.log4j.Logger;
import org.prot.appserver.app.AppInfo;

public class NullAppFetcher implements AppFetcher
{
	private static final Logger logger = Logger.getLogger(NullAppFetcher.class);

	@Override
	public AppInfo fetchApp(String appId)
	{
		// Does nothing because the application is already available

		AppInfo appInfo = new AppInfo();
		appInfo.setAppId(appId);
		return appInfo;
	}
}

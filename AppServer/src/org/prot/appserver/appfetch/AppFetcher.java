package org.prot.appserver.appfetch;

import org.prot.appserver.app.AppInfo;

public interface AppFetcher
{
	public AppInfo fetchApp(String appId);
}

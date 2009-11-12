package org.prot.appserver.appfetch;

import org.prot.appserver.app.AppInfo;

public interface AppFetcher
{
	/**
	 * Load the WAR-Archife for the application with the given AppId. Create a
	 * new AppInfo-Object which contains the bytes of the WAR-File and the
	 * AppId.
	 * 
	 * @param appId
	 * @return
	 */
	public AppInfo fetchApp(String appId);
}

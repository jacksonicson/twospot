package org.prot.controller.app.lifecycle.appfetch;


public interface AppFetcher {
	/**
	 * Load the WAR-Archife for the application with the given AppId. Create a
	 * new AppInfo-Object which contains the bytes of the WAR-File and the
	 * AppId.
	 * 
	 * @param appId
	 * @return
	 */
	public byte[] fetchApp(String appId);
}

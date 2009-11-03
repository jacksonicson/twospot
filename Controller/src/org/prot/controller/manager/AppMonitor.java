package org.prot.controller.manager;

import java.util.Hashtable;
import java.util.Map;

public class AppMonitor
{
	private Map<AppInfo, AppProcess> processList = new Hashtable<AppInfo, AppProcess>();

	public void registerProcess(AppProcess process)
	{
		this.processList.put(process.getOwner(), process);
	}

	public AppProcess getProcess(AppInfo appInfo)
	{
		return processList.get(appInfo);
	}
}

package org.prot.controller.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

class AppRegistry
{
	private final int startPort = 9090;

	private int currentPort = startPort;

	private Stack<Integer> freePorts = new Stack<Integer>();

	private Map<String, AppInfo> appInfos = new HashMap<String, AppInfo>();

	private int getPort()
	{
		if (freePorts.isEmpty())
			return this.currentPort++;

		return freePorts.peek();
	}

	private void putApp(AppInfo appInfo)
	{
		this.appInfos.put(appInfo.getAppId(), appInfo);
	}

	public synchronized AppInfo getAppInfo(String appId)
	{
		return appInfos.get(appId);
	}

	public synchronized void deleteApp(String appId)
	{
		if (appInfos.containsKey(appId))
		{
			AppInfo appInfo = appInfos.get(appId);
			freePorts.add(appInfo.getPort());
			appInfos.remove(appId);
		}
	}

	public synchronized AppInfo getOrRegisterApp(String appId)
	{
		AppInfo appInfo = appInfos.get(appId);
		if (appInfo != null)
		{
			appInfo.tick();
			return appInfo;
		}

		appInfo = new AppInfo(appId, getPort());
		appInfo.tick();
		putApp(appInfo);

		return appInfo;
	}

	public Set<AppInfo> tick()
	{
		Set<AppInfo> idleApps = null;
		for (AppInfo info : appInfos.values())
		{
			if (info.isIdle())
			{
				if (idleApps == null)
					idleApps = new HashSet<AppInfo>();

				info.setStatus(AppState.KILLED);
				
				synchronized (this)
				{
					appInfos.remove(info.getAppId());
					idleApps.add(info);
				}
			}
		}

		return idleApps;
	}
}

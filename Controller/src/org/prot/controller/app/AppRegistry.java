package org.prot.controller.app;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AppRegistry implements TokenChecker
{
	private final PortPool portPool = new PortPool();

	private Map<String, AppInfo> appInfos = new ConcurrentHashMap<String, AppInfo>();

	private Map<String, Long> blocked = new ConcurrentHashMap<String, Long>();

	public AppInfo getAppInfo(String appId)
	{
		return appInfos.get(appId);
	}

	private void deleteApp(String appId)
	{
		if (!appInfos.containsKey(appId))
			return;

		synchronized (appInfos)
		{
			if (!appInfos.containsKey(appId))
				return;

			AppInfo appInfo = appInfos.get(appId);
			portPool.releasePort(appInfo.getPort());

			appInfos.remove(appId);
		}
	}

	public boolean isBlocked(String appId)
	{
		if (!blocked.containsKey(appId))
			return false;

		Long time = blocked.get(appId);
		if (time == null)
			return false;

		if (System.currentTimeMillis() - time > 50000)
		{
			blocked.remove(appId);
			return false;
		}

		return true;
	}

	public AppInfo getOrRegisterApp(String appId)
	{
		AppInfo appInfo = appInfos.get(appId);
		if (appInfo != null)
			return appInfo;

		synchronized (appInfos)
		{
			// We need to recheck this to be sure no other thread has created
			// one
			if (appInfos.containsKey(appId))
				return appInfos.get(appId);

			// Get a port
			int port = portPool.getPort();

			// Create new AppInfo
			appInfo = new AppInfo(appId, port);

			// Add the new AppInfo
			this.appInfos.put(appInfo.getAppId(), appInfo);
		}

		return appInfo;
	}

	void updateStates()
	{
		// Check for idle apps
		for (AppInfo info : appInfos.values())
		{
			AppState state = info.getStatus();
			switch (state)
			{
			case FAILED:
				// TODO - migrate to KILLED
				break;
			}
		}
	}

	void removeDead()
	{
		for (Iterator<String> it = appInfos.keySet().iterator(); it.hasNext();)
		{
			String appId = it.next();
			AppInfo info = appInfos.get(appId);

			if (info == null)
				continue;

			if (info.getStatus() == AppState.DEAD)
				it.remove();
		}
	}

	Set<AppInfo> kill()
	{
		Set<AppInfo> killedApps = new HashSet<AppInfo>();

		// Check for idle apps
		for (AppInfo info : appInfos.values())
		{
			AppState state = info.getStatus();
			switch (state)
			{
			case BANNED:
			case KILLED:
			case DEPLOYED:
				killedApps.add(info);
				break;
			}
		}

		// Remove dead apps
		removeDead();

		// Return list of killed apps
		return killedApps;
	}

	@Override
	public boolean checkToken(String token)
	{
		// False if there is no token
		if (token == null)
			return false;

		// Iterate over all running applications
		for (String appId : appInfos.keySet())
		{
			// Get application infos and the token
			AppInfo info = appInfos.get(appId);

			// Concurrency - AppInfo could be deleted while scanning the AppId's
			if (info == null)
				continue;

			// Compare stored token
			if (token.equals(info.getProcessToken()))
			{
				// If both tokens are equal - return true
				return true;
			}
		}

		// No matching token found
		return false;
	}

	public Set<AppInfo> getAppInfos()
	{
		Set<AppInfo> appInfos = new HashSet<AppInfo>();
		appInfos.addAll(this.appInfos.values());
		return appInfos;
	}

	public Set<String> getAppIds()
	{
		return appInfos.keySet();
	}

	public Set<String> getDuplicatedAppIds()
	{
		Set<String> duplicate = new HashSet<String>();
		duplicate.addAll(appInfos.keySet());
		return duplicate;
	}
}

package org.prot.controller.app;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

	private void cleanup()
	{
		List<AppInfo> copy = new ArrayList<AppInfo>();
		synchronized (appInfos)
		{
			copy.addAll(appInfos.values());
		}

		Set<AppInfo> toDelete = new HashSet<AppInfo>();
		for (AppInfo info : copy)
		{
			AppState state = info.getStatus();
			switch (state)
			{
			case OFFLINE:
				toDelete.add(info);
				continue;
			}
		}

		// The deleteApp method is multithreaded
		for (AppInfo info : toDelete)
			deleteApp(info.getAppId());
	}

	Set<AppInfo> findDeadApps()
	{
		Set<AppInfo> idleApps = null;

		// Check for idle apps
		for (AppInfo info : appInfos.values())
		{
			AppState state = info.getStatus();
			if (state == AppState.FAILED || state == AppState.KILLED || state == AppState.IDLE)
			{
				if (idleApps == null)
					idleApps = new HashSet<AppInfo>();

				synchronized (info)
				{
					// Double check if the app is really dead
					// Perhaps we don't get all dead apps here - but in the next
					// run we will get all left apps.
					if (info.getStatus().equals(state))
					{
						switch (info.getStatus())
						{
						case FAILED:
							info.setStatus(AppState.KILLED);
						case KILLED:
							blocked.put(info.getAppId(), System.currentTimeMillis());
							break;
						case IDLE:
							break;
						}

						idleApps.add(info);
					}
				}
			}
		}

		// Remove everything
		cleanup();

		// Return list of idle apps
		return idleApps;
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

package org.prot.controller.app;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class AppRegistry implements TokenChecker
{
	private static final Logger logger = Logger.getLogger(AppRegistry.class);

	private final PortPool portPool = new PortPool();

	private List<AppInfo> appInfos = new ArrayList<AppInfo>();

	private Map<String, AppInfo> appMapping = new ConcurrentHashMap<String, AppInfo>();

	private Map<String, Long> blocked = new ConcurrentHashMap<String, Long>();

	public AppInfo getAppInfo(String appId)
	{
		return appMapping.get(appId);
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

	private boolean isUsableState(AppState state)
	{
		switch (state)
		{
		case NEW:
		case STARTING:
		case ONLINE:
			return true;
		}

		return false;
	}

	public AppInfo getOrRegisterApp(String appId)
	{
		// Fast path
		AppInfo appInfo = appMapping.get(appId);
		if (appInfo != null && isUsableState(appInfo.getStatus()))
			return appInfo;

		synchronized (appInfos)
		{
			// We need to recheck this to be sure no other thread has created
			// one
			if (appMapping.containsKey(appId) && isUsableState(appInfo.getStatus()))
				return appMapping.get(appId);

			// Get a port
			int port = portPool.getPort();

			// Create new AppInfo
			appInfo = new AppInfo(appId, port);

			// Add the new AppInfo
			appInfos.add(appInfo);
			appMapping.put(appId, appInfo);
		}

		return appInfo;
	}

	void updateStates()
	{
		// Check for idle apps
		synchronized (appInfos)
		{
			for (AppInfo info : appInfos)
			{
				logger.debug("Updating app " + info.getAppId() + " state " + info.getStatus());

				synchronized (info)
				{
					AppState state = info.getStatus();
					switch (state)
					{
					case DEPLOYED:
						info.setStatus(AppState.KILLED);
						break;

					case BANNED:
						blocked.put(info.getAppId(), System.currentTimeMillis());
						info.setStatus(AppState.KILLED);
						break;
					}

				}
			}
		}
	}

	void removeDead()
	{
		synchronized (appInfos)
		{
			for (Iterator<AppInfo> it = appInfos.iterator(); it.hasNext();)
			{
				AppInfo info = it.next();

				if (info == null)
					continue;

				if (info.getStatus() == AppState.DEAD)
				{
					logger.debug("Removing: " + info.getAppId() + " sate: " + info.getStatus());
					it.remove();

					// We clearly check object instances here!
					if (appMapping.get(info.getAppId()) == info)
						appMapping.remove(info.getAppId());
				}
			}
		}
	}

	Set<AppInfo> kill()
	{
		Set<AppInfo> killedApps = new HashSet<AppInfo>();

		// Check for idle apps
		synchronized (appInfos)
		{
			for (AppInfo info : appInfos)
			{
				synchronized (info)
				{
					AppState state = info.getStatus();
					switch (state)
					{
					case KILLED:
						info.setStatus(AppState.DEAD);
						killedApps.add(info);
						break;
					}

				}
			}
		}

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
		for (Iterator<AppInfo> it = appInfos.iterator(); it.hasNext();)
		{
			// Get application infos and the token
			AppInfo info = it.next();

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

	public Set<String> getAppIds()
	{
		return appMapping.keySet();
	}

	public Set<AppInfo> getDuplicatedAppInfos()
	{
		Set<AppInfo> appInfos = new HashSet<AppInfo>();
		appInfos.addAll(appMapping.values());
		return appInfos;
	}

	public Set<String> getDuplicatedAppIds()
	{
		Set<String> duplicate = new HashSet<String>();
		duplicate.addAll(appMapping.keySet());
		return duplicate;
	}
}

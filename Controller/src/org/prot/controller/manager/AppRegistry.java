package org.prot.controller.manager;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

class AppRegistry
{
	private static final Logger logger = Logger.getLogger(AppRegistry.class);

	private final int startPort = 9090;

	private int currentPort = startPort;

	private Queue<Integer> freePorts = new LinkedList<Integer>();

	private Map<String, AppInfo> appInfos = new ConcurrentHashMap<String, AppInfo>();

	private synchronized int getPort()
	{
		// Check if there are any free ports
		if (freePorts.isEmpty())
			return this.currentPort++;

		// Find a free port
		synchronized (freePorts)
		{
			Integer foundPort = null;
			for (Integer test : freePorts)
			{
				try
				{
					ServerSocket socket = new ServerSocket(test);
					socket.close();
					foundPort = test;
					break;
				} catch (Exception e)
				{
					logger.warn("AppRegsitry could not reuse port: " + test);
					continue;
				}
			}

			if (foundPort != null)
			{
				freePorts.remove(foundPort);
				return foundPort;
			}
		}

		return this.currentPort++;
	}

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
			freePorts.add(appInfo.getPort());

			appInfos.remove(appId);
		}
	}

	public AppInfo getOrRegisterApp(String appId)
	{
		AppInfo appInfo = appInfos.get(appId);
		if (appInfo != null)
		{
			appInfo.touch();
			return appInfo;
		}

		synchronized (appInfos)
		{
			// We need to recheck this to be sure no other thread has created
			// one
			if (appInfos.containsKey(appId))
				return appInfos.get(appId);

			// Create new AppInfo
			appInfo = new AppInfo(appId, getPort());
			appInfo.touch();

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
			case KILLED:
			case STALE:
			case FAILED:
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
			if (info.isIdle() || state == AppState.FAILED || state == AppState.KILLED)
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
						info.setStatus(AppState.KILLED);
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

	Set<String> getAppIds()
	{
		return appInfos.keySet();
	}
}

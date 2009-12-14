package org.prot.controller.manager;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;

class AppRegistry
{
	private static final Logger logger = Logger.getLogger(AppRegistry.class);

	private final int startPort = 9090;

	private int currentPort = startPort;

	private Queue<Integer> freePorts = new LinkedList<Integer>();

	private Map<String, AppInfo> appInfos = new HashMap<String, AppInfo>();

	private int getPort()
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
			appInfo.touch();
			return appInfo;
		}

		appInfo = new AppInfo(appId, getPort());
		appInfo.touch();
		putApp(appInfo);

		return appInfo;
	}

	private void cleanup()
	{
		List<AppInfo> copy = new ArrayList<AppInfo>();
		synchronized (this)
		{
			copy.addAll(appInfos.values());
		}

		Set<AppInfo> toDelete = new HashSet<AppInfo>();
		for (AppInfo info : copy)
		{
			synchronized (info)
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
		}

		synchronized (this)
		{
			for (AppInfo info : toDelete)
			{
				deleteApp(info.getAppId());
			}
		}
	}

	Set<AppInfo> findDeadApps()
	{
		Set<AppInfo> idleApps = null;

		// Create a copy to prevent concurrent modifications
		Set<AppInfo> values = new HashSet<AppInfo>();
		synchronized (appInfos)
		{
			values.addAll(appInfos.values());
		}

		// Check for idle apps
		for (AppInfo info : values)
		{
			synchronized (info)
			{
				AppState state = info.getStatus();
				if (info.isIdle() || state == AppState.FAILED || state == AppState.KILLED)
				{
					if (idleApps == null)
						idleApps = new HashSet<AppInfo>();

					info.setStatus(AppState.KILLED);
					idleApps.add(info);
				}
			}
		}

		// Remove everything
		cleanup();

		// Return list of idle apps
		return idleApps;
	}

	synchronized Set<String> getAppIds()
	{
		return appInfos.keySet();
	}
}

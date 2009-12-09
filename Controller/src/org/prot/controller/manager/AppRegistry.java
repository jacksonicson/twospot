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
		List<Integer> testedPorts = new ArrayList<Integer>();
		int port = -1;
		while (freePorts.isEmpty() == false)
		{
			port = freePorts.poll();
			try
			{
				ServerSocket socket = new ServerSocket(port);
				socket.close();
			} catch (Exception e)
			{
				logger.warn("AppRegsitry could not reuse port: " + port);
				testedPorts.add(port);
				port = -1;
				continue;
			}

			break;
		}

		// Add all failed ports to the queue
		freePorts.addAll(testedPorts);

		// Did we find a port - if not we use a new port!
		if (port == -1)
			port = this.currentPort++;

		return port;
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

	private void cleanup()
	{
		List<AppInfo> copy = null;
		synchronized (this)
		{
			copy = new ArrayList<AppInfo>();
			copy.addAll(appInfos.values());
		}

		Set<AppInfo> delete = new HashSet<AppInfo>();
		for (AppInfo info : copy)
		{
			AppState state = info.getStatus();
			switch (state)
			{
			case KILLED:
			case STALE:
			case FAILED:
				delete.add(info);
				continue;
			}
		}

		synchronized (this)
		{
			for (AppInfo info : delete)
			{
				deleteApp(info.getAppId());
			}
		}
	}

	Set<AppInfo> tick()
	{
		Set<AppInfo> idleApps = null;
		for (AppInfo info : appInfos.values())
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

		// Remove everything
		cleanup();

		return idleApps;
	}

	synchronized Set<String> getAppIds()
	{
		return appInfos.keySet();
	}
}

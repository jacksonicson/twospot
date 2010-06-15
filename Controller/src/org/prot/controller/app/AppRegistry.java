/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
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

	// Ports for new AppServers
	private final PortPool portPool = new PortPool();

	// List of all managed AppServers or applications
	private List<AppInfo> appInfos = new ArrayList<AppInfo>();

	// Mapping between the appId and AppServer. The Map always points to the
	// current AppServer for an application
	private Map<String, AppInfo> appMapping = new ConcurrentHashMap<String, AppInfo>();

	// List of all blocked appId's and there timestamp of blocking
	private Map<String, Long> blocked = new ConcurrentHashMap<String, Long>();

	public AppInfo getAppInfo(String appId)
	{
		synchronized (appInfos)
		{
			return appMapping.get(appId);
		}
	}

	public boolean isBlocked(String appId)
	{
		if (!blocked.containsKey(appId))
			return false;

		synchronized (blocked)
		{
			Long time = blocked.get(appId);
			if (time == null)
			{
				blocked.remove(appId);
				return false;
			}

			if ((System.currentTimeMillis() - time) > 30000)
			{
				blocked.remove(appId);
				return false;
			}

			logger.debug("Blocking " + appId);
			return true;
		}
	}

	public AppInfo getOrRegisterApp(String appId)
	{
		// Fast path: Check if there is an AppInfo for the appId in the FIRST
		// life
		AppInfo appInfo = appMapping.get(appId);
		if (appInfo != null && appInfo.getStatus().getLife() == AppLife.FIRST)
			return appInfo;

		// Did not find an AppInfo object or an AppInfo object in a usable state
		synchronized (appInfos)
		{
			// Check again (synchronized)
			appInfo = appMapping.get(appId);
			if (appInfo != null && appInfo.getStatus().getLife() == AppLife.FIRST)
				return appMapping.get(appInfo);

			if (appInfo != null)
				logger.debug("Creating new AppInfo object - current one has the state: "
						+ appInfo.getStatus());

			// Port for the new AppServer
			int port = portPool.getPort();

			// Create a new AppInfo object
			appInfo = new AppInfo(appId, port);

			// Add the AppInfo to the list and map
			appInfos.add(appInfo);
			appMapping.put(appId, appInfo);
		}

		return appInfo;
	}

	void updateStates()
	{
		// Method is always executed within the same thread as
		// removeDeadAppInfos(). There is no need for a synchronization on
		// appInfos.

		synchronized (appInfos)
		{
			// Iterate over all AppInfo objects
			for (AppInfo info : appInfos)
			{
				logger.debug("Updating app " + info.getAppId() + " state " + info.getStatus());
				synchronized (info)
				{
					// Do a state transition if necessary
					AppState state = info.getStatus();

					// Time since the last touch
					long lastTouch = System.currentTimeMillis() - info.getTouch();

					switch (state)
					{
					case DEPLOYED:
						// Wait for all Requests to finish
						if (lastTouch > 60000 || info.getActiveRequests() <= 0)
							info.setState(AppState.KILLED);
						break;

					case DROPPED:
						// Wait for all Requests to finish
						if (lastTouch > 60000 || info.getActiveRequests() <= 0)
							info.setState(AppState.KILLED);
						break;

					case BANNED:
						// Add the appId to the block list
						blocked.put(info.getAppId(), System.currentTimeMillis());
						info.setState(AppState.KILLED);
						break;
					}
				}
			}
		}
	}

	void removeDeadAppInfos()
	{
		synchronized (appInfos)
		{
			// Iterate over all AppInfo objects
			for (Iterator<AppInfo> it = appInfos.iterator(); it.hasNext();)
			{
				// Must not be null
				AppInfo info = it.next();

				// Check if the AppInfo state is DEAD
				if (info.getStatus() == AppState.DEAD)
				{
					logger.debug("Removing: " + info.getAppId() + " sate: " + info.getStatus());

					// Release the port
					portPool.releasePort(info.getPort());

					// Remove the AppInfo from the list
					it.remove();

					// Check if the AppInfo is linked in the map
					if (appMapping.get(info.getAppId()) == info)
						appMapping.remove(info.getAppId());
				}
			}
		}
	}

	List<AppInfo> killDeadAppInfos()
	{
		synchronized (appInfos)
		{
			// List of all killed AppInfos
			List<AppInfo> killedApps = new ArrayList<AppInfo>();

			// Check for idle apps
			for (AppInfo info : appInfos)
			{
				synchronized (info)
				{
					AppState state = info.getStatus();
					switch (state)
					{
					case KILLED:
						info.setState(AppState.DEAD);
						killedApps.add(info);
						break;
					}

				}
			}

			// Return list of killed apps
			return killedApps;
		}
	}

	@Override
	public boolean checkToken(String token)
	{
		// False if there is no token
		if (token == null)
			return false;

		synchronized (appInfos)
		{
			// Iterate over all running applications
			for (AppInfo info : appInfos)
			{
				// Compare stored token
				if (token.equals(info.getProcessToken()))
				{
					// If both tokens are equal - return true
					return true;
				}
			}
		}

		// No matching token found
		return false;
	}

	public Set<AppInfo> getDuplicatedAppInfos()
	{
		synchronized (appInfos)
		{
			Set<AppInfo> appInfos = new HashSet<AppInfo>();
			appInfos.addAll(appMapping.values());
			return appInfos;
		}
	}
}

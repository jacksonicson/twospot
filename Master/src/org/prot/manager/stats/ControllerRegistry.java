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
package org.prot.manager.stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.prot.util.managment.gen.ManagementData;

public class ControllerRegistry
{
	private static final Logger logger = Logger.getLogger(ControllerRegistry.class);

	private Map<String, ControllerStats> publicControllers = new HashMap<String, ControllerStats>();

	private Map<String, ControllerInfo> controllerInfos = new ConcurrentHashMap<String, ControllerInfo>();

	private Map<String, ControllerStats> controllers = new ConcurrentHashMap<String, ControllerStats>();

	public Map<String, ControllerInfo> getControllerInfos()
	{
		synchronized (controllerInfos)
		{
			Map<String, ControllerInfo> copy = new HashMap<String, ControllerInfo>();
			copy.putAll(controllerInfos);
			return copy;
		}
	}

	public boolean containsControllerInfo(String address)
	{
		synchronized (controllerInfos)
		{
			return controllerInfos.containsKey(address);
		}
	}

	public void update(List<ControllerInfo> infos)
	{
		synchronized (controllerInfos)
		{
			// Contains alls addresses from the controllers
			Set<String> availableAddresses = new HashSet<String>();

			// Update existing and add new controllers
			for (ControllerInfo info : infos)
			{
				String address = info.getAddress();
				availableAddresses.add(address);

				if (controllers.containsKey(address))
				{
					logger.info("Updating Controller: " + address);
					ControllerInfo toUpdate = controllerInfos.get(address);
					toUpdate.update(info);
				} else
				{
					logger.info("New Controller: " + address);
					controllerInfos.put(address, info);
				}
			}

			// Remove all controllers which are not in the availableAddress list
			for (Iterator<String> itController = controllers.keySet().iterator(); itController.hasNext();)
			{
				String testAddress = itController.next();
				if (availableAddresses.contains(testAddress))
					continue;

				logger.debug("Removing Controller: " + testAddress);
				itController.remove();
			}
		}
	}

	public synchronized void finalizeUpdate()
	{
		// Remove all old controllers
		removeOld();

		synchronized (publicControllers)
		{
			// Clear the public controllers
			publicControllers.clear();

			// Update public controllers
			publicControllers.putAll(controllers);
		}
	}

	public synchronized void assignToController(String appId, String address)
	{
		synchronized (controllers)
		{
			ControllerStats stats = controllers.get(address);
			if (stats == null)
				return;

			stats.assign(appId);
		}
	}

	public void removeController(String address)
	{
		synchronized (controllers)
		{
			controllers.remove(address);
		}
	}

	public void updateController(ManagementData.Controller managementController)
	{
		synchronized (controllers)
		{
			String address = managementController.getAddress();
			ControllerStats controller = controllers.get(address);
			if (controller == null)
			{
				controller = new ControllerStats(address);
				controllers.put(address, controller);
			}

			controller.updateStats(managementController);
		}
	}

	private void removeOld()
	{
		synchronized (controllers)
		{
			for (Iterator<String> it = controllers.keySet().iterator(); it.hasNext();)
			{
				String address = it.next();
				ControllerStats controller = controllers.get(address);
				if (controller.isOld())
					it.remove();
			}
		}
	}

	public boolean isEmpty()
	{
		synchronized (publicControllers)
		{
			return publicControllers.isEmpty();
		}
	}

	public Map<String, ControllerStats> getControllers()
	{
		synchronized (publicControllers)
		{
			Map<String, ControllerStats> copy = new HashMap<String, ControllerStats>();
			copy.putAll(publicControllers);
			return copy;
		}
	}

	public void dump()
	{
		for (ControllerStats controller : controllers.values())
			controller.dump();
	}
}

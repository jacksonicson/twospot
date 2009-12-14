package org.prot.manager.watcher;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.prot.controller.management.services.IJmxResources;
import org.prot.manager.data.ControllerInfo;
import org.prot.manager.data.ControllerRegistry;
import org.prot.manager.data.ManagementData;
import org.prot.util.scheduler.Scheduler;
import org.prot.util.scheduler.SchedulerTask;

public class ControllerWatcher
{
	private static final Logger logger = Logger.getLogger(ControllerWatcher.class);

	private ControllerRegistry registry;

	private Map<String, JmxController> connections = new HashMap<String, JmxController>();

	public void init()
	{
		Scheduler.addTask(new WatchTask());
	}

	private JmxController getJmxController(String address)
	{
		JmxController connection = connections.get(address);
		if (connection == null)
		{
			connection = new JmxController(address);
			connections.put(address, connection);
		}

		return connection;
	}

	private void removeJmxController(String address)
	{
		JmxController connection = getJmxController(address);
		connection.release();
		connections.remove(address);
	}

	private void update()
	{
		// Iterate over all controllers
		for (ControllerInfo info : registry.getControllers())
		{
			try
			{
				logger.debug("Querying Controller: " + info.getServiceAddress());

				// Get JMX connection
				JmxController connection = getJmxController(info.getServiceAddress());
				ManagementData management = info.getManagementData();

				// Update resource data
				IJmxResources resources = connection.getJmxResources();
				updateResources(management, resources);

			} catch (Exception e)
			{
				removeJmxController(info.getServiceAddress());
			}
		}
	}

	private void updateResources(ManagementData management, IJmxResources resources)
	{
		management.updateRunningApps(resources.getApps());
		management.updatePerformanceData(resources.getAppsPerformance());

		management.setRps(resources.requestsPerSecond());
		management.setMemLoad(resources.freeMemory());
		management.setAverageCpu(resources.loadAverage());

		management.dump();
	}

	class WatchTask extends SchedulerTask
	{
		@Override
		public void run()
		{
			update();
		}

		@Override
		public long getInterval()
		{
			return 5000;
		}
	}

	public void setRegistry(ControllerRegistry registry)
	{
		this.registry = registry;
	}
}

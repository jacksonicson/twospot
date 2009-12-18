package org.prot.manager.watcher;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.prot.manager.stats.ControllerInfo;
import org.prot.manager.stats.ControllerRegistry;
import org.prot.manager.stats.Stats;
import org.prot.util.managment.Ping;
import org.prot.util.scheduler.Scheduler;
import org.prot.util.scheduler.SchedulerTask;

public class ControllerWatcher
{
	private static final Logger logger = Logger.getLogger(ControllerWatcher.class);

	private ControllerRegistry registry;

	private Stats stats;

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

	private void removeController(String address)
	{
		JmxController connection = getJmxController(address);
		connection.release();
		connections.remove(address);

		stats.remove(address);
	}

	private void update()
	{
		stats.startUpdate();

		// Iterate over all controllers
		for (ControllerInfo info : registry.getControllers())
		{
			try
			{
				logger.debug("Querying Controller: " + info.getServiceAddress());

				// Get JMX connection
				JmxController connection = getJmxController(info.getServiceAddress());
				Ping ping = connection.getJmxResources();

				stats.update(info.getAddress(), ping);

			} catch (Exception e)
			{
				removeController(info.getServiceAddress());
			}
		}

		stats.finalizeUpdate();
		stats.dump();
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

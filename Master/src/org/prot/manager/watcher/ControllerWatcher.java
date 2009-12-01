package org.prot.manager.watcher;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.prot.controller.management.jmx.IJmxDeployment;
import org.prot.controller.management.jmx.IJmxResources;
import org.prot.manager.data.ControllerInfo;
import org.prot.manager.data.ControllerRegistry;
import org.prot.manager.data.ManagementData;

public class ControllerWatcher
{
	private static final Logger logger = Logger.getLogger(ControllerWatcher.class);

	private ControllerRegistry registry;

	private Timer timer;

	private Map<String, JmxControllerConnection> connections = new HashMap<String, JmxControllerConnection>();

	public void init()
	{
		timer = new Timer();
		timer.scheduleAtFixedRate(new WatchTask(), 0, 2000);
	}

	private JmxControllerConnection getConnection(String address)
	{
		JmxControllerConnection connection = connections.get(address);
		if (connection == null)
		{
			connection = new JmxControllerConnection(address);
			connections.put(address, connection);
		}

		return connection;
	}

	private void removeConnection(String address)
	{
		connections.remove(address);
	}

	private void collectManagementData()
	{
		logger.debug("collecting management data");

		Set<String> redeployedApps = new HashSet<String>();

		Collection<ControllerInfo> controllers = registry.getControllers();
		for (ControllerInfo info : controllers)
		{
			try
			{
				logger.info("from controller: " + info.getServiceAddress());

				JmxControllerConnection connection = getConnection(info.getServiceAddress());

				// Get redeployed apps
				IJmxDeployment deploy = connection.getJmxDeployment();

				List<String> ctrlRedeployedApps = deploy.getDeployedApps();
				for (String app : ctrlRedeployedApps)
				{
					if (redeployedApps.contains(app) == false)
					{
						logger.debug("Redeployed app: " + app);
						redeployedApps.addAll(ctrlRedeployedApps);
					}
				}

				// Aquire management data
				ManagementData management = info.getManagementData();
				IJmxResources resources = connection.getJmxResources();

				logger.info("Ressources: " + resources.getName());
			} catch (Exception e)
			{
				logger.info("Removing controller from list: " + info.getServiceAddress());
				removeConnection(info.getServiceAddress());
			}
		}

		executeRedeployedApps(redeployedApps);
	}

	private void executeRedeployedApps(Set<String> redeployedApps)
	{
		logger.info("executing redeployed apps");
	}

	class WatchTask extends TimerTask
	{
		@Override
		public void run()
		{
			collectManagementData();
		}
	}

	public void setRegistry(ControllerRegistry registry)
	{
		this.registry = registry;
	}
}

package org.prot.manager.watcher;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
		// Used to collect all deployments
		Set<String> deployments = new HashSet<String>();

		// Iterate over all known controllers
		Collection<ControllerInfo> controllers = registry.getControllers();
		for (ControllerInfo info : controllers)
		{
			try
			{
				logger.info("Loading management data from Controller: " + info.getServiceAddress());

				// Get the connection to the controller
				JmxControllerConnection connection = getConnection(info.getServiceAddress());

				// Get redeployed apps
				IJmxDeployment deploy = connection.getJmxDeployment();
				Set<String> ctrlRedeployedApps = deploy.fetchDeployedApps();
				for (String app : ctrlRedeployedApps)
				{
					if (deployments.contains(app) == false)
					{
						logger.debug("Redeployed app: " + app);
						deployments.add(app);
					}
				}

				// Aquire management data
				ManagementData management = info.getManagementData();
				IJmxResources resources = connection.getJmxResources();
				management.setRunningApps(resources.getApps());
				management.setRps(resources.requestsPerSecond());

			} catch (Exception e)
			{
				// Connection lost - remove the connection
				logger.info("Removing controller from list: " + info.getServiceAddress());
				logger.debug("Cause: " + e);
				removeConnection(info.getServiceAddress());
			}
		}

		// Inform all controllers about the deployments
		if (deployments.isEmpty() == false)
			executeRedeployedApps(deployments);
	}

	private void executeRedeployedApps(Set<String> redeployedApps)
	{
		// Iterate over all known controllers
		Collection<ControllerInfo> controllers = registry.getControllers();
		for (ControllerInfo info : controllers)
		{
			try
			{
				// Get the JMX-Connection to the controller
				JmxControllerConnection connection = getConnection(info.getServiceAddress());

				// Inform the controller about the deployment
				logger.info("Informing Controller about the deployments");
				connection.getJmxDeployment().notifyDeployment(redeployedApps);

			} catch (Exception e)
			{
				// Connection lost - remove the connection
				logger.info("Removing controller from list: " + info.getServiceAddress());
				logger.debug("Cause", e);
				removeConnection(info.getServiceAddress());
			}
		}
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

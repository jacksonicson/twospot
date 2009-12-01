package org.prot.manager.watcher;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.prot.controller.management.jmx.IJmxDeployment;
import org.prot.manager.data.ControllerInfo;
import org.prot.manager.data.ControllerRegistry;

public class ControllerWatcher
{
	private static final Logger logger = Logger.getLogger(ControllerWatcher.class);

	private ControllerRegistry registry;

	private Timer timer;

	public void init()
	{
		timer = new Timer();
		timer.scheduleAtFixedRate(new WatchTask(), 0, 5000);
	}

	class WatchTask extends TimerTask
	{
		@Override
		public void run()
		{
			Collection<ControllerInfo> controllers = registry.getControllers();
			for (ControllerInfo info : controllers)
			{
				logger.info("checking controller: " + info.getAddress());

				IJmxDeployment deploy = (IJmxDeployment) ExceptionSafeProxy.newInstance(getClass()
						.getClassLoader(), IJmxDeployment.class, "localhost", "bean:name=deployment");

				logger.info("deployed names: " + deploy.getName());
			}
		}
	}

	public void setRegistry(ControllerRegistry registry)
	{
		this.registry = registry;
	}
}

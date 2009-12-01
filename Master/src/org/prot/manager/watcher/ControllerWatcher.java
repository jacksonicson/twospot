package org.prot.manager.watcher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;

import org.apache.log4j.Logger;
import org.prot.controller.management.jmx.IJmxDeployment;
import org.prot.manager.data.ControllerInfo;
import org.prot.manager.data.ControllerRegistry;
import org.springframework.jmx.access.MBeanProxyFactoryBean;

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
			logger.info("timer task is running");

			Collection<ControllerInfo> controllers = registry.getControllers();
			for (ControllerInfo info : controllers)
			{
				// Establish a new JMX-Connection
				org.springframework.jmx.support.MBeanServerConnectionFactoryBean connection = new org.springframework.jmx.support.MBeanServerConnectionFactoryBean();
				try
				{
					connection.setServiceUrl("service:jmx:rmi:///jndi/rmi://localhost:2299/server");

					connection.afterPropertiesSet();

				} catch (MalformedURLException e)
				{
					e.printStackTrace();
					continue;
				} catch (IOException e)
				{
					e.printStackTrace();
				}

				MBeanProxyFactoryBean proxy = new MBeanProxyFactoryBean();
				try
				{
					proxy.setObjectName("bean:name=testBean");
					proxy.setProxyInterface(IJmxDeployment.class);
					proxy.setServer((MBeanServerConnection) connection.getObject());
					proxy.afterPropertiesSet();
					IJmxDeployment dep = (IJmxDeployment) proxy.getObject();
					if (dep != null)
					{
						
						
					} else
						logger.error("connection is null");

				} catch (MalformedObjectNameException e)
				{
					e.printStackTrace();
				}

				logger.info("checking controller: " + info.getAddress());
			}
		}
	}

	public void setRegistry(ControllerRegistry registry)
	{
		this.registry = registry;
	}
}

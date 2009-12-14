package org.prot.controller.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;

public class WatchApp implements Job, Watcher
{
	private static final Logger logger = Logger.getLogger(WatchApp.class);

	private ZooHelper zooHelper;

	private Set<String> watching = new HashSet<String>();

	private List<DeploymentListener> listeners = new ArrayList<DeploymentListener>();

	public void addDeploymentListener(DeploymentListener listener, String appId)
	{
		synchronized (listeners)
		{
			if (listeners.contains(listener) == false)
				listeners.add(listener);

			watching.add(appId);

			zooHelper.getQueue().insert(this);
		}
	}

	private void appDeployed(String appId)
	{
		List<DeploymentListener> copy = new ArrayList<DeploymentListener>();
		synchronized (listeners)
		{
			copy.addAll(listeners);
		}

		for (DeploymentListener listener : copy)
			listener.appDeployed(appId);
	}

	@Override
	public void process(WatchedEvent event)
	{
		try
		{
			ZooKeeper zk = zooHelper.getZooKeeper();
			String path = event.getPath();

			Stat stat = new Stat();
			byte[] data = zk.getData(path, false, stat);
			String appId = new String(data);

			if (watching.contains(appId))
				appDeployed(appId);

		} catch (InterruptedException e)
		{
			logger.error(e);
		} catch (IOException e)
		{
			logger.error(e);
		} catch (KeeperException e)
		{
			logger.error(e);
		}

		// Reschedule this task
		zooHelper.getQueue().insert(this);
	}

	@Override
	public boolean execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		ZooKeeper zk = zooHelper.getZooKeeper();

		String path = ZNodes.ZNODE_APPS;
		try
		{
			List<String> childs = zk.getChildren(path, false);
			for (String child : childs)
			{
				Stat stat = new Stat();
				byte[] data = zk.getData(child, false, stat);

				String childAppId = new String(data);
				if (watching.contains(childAppId))
				{
					zk.exists(path + "/" + child, this);
				}
			}

		} catch (KeeperException e)
		{
			// Retry
			return false;
		} catch (InterruptedException e)
		{
			// Retry
			return false;
		}

		return true;
	}

	@Override
	public void init(ZooHelper zooHelper)
	{
		this.zooHelper = zooHelper;
	}

	@Override
	public boolean isRetryable()
	{
		return true;
	}
}

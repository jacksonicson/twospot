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
import org.apache.zookeeper.Watcher.Event.EventType;
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

	public void addDeploymentListener(DeploymentListener listener)
	{
		synchronized (listeners)
		{
			listeners.add(listener);
		}
	}

	public void watchApp(String appId)
	{
		logger.debug("Watching AppId: " + appId);

		watching.add(appId);
		zooHelper.getQueue().insert(this);
	}

	public void removeWatch(String appId)
	{
		logger.debug("Don't watch AppId: " + appId);

		watching.remove(appId);
	}

	private void appDeployed(String appId)
	{
		List<DeploymentListener> copy = new ArrayList<DeploymentListener>();
		synchronized (listeners)
		{
			copy.addAll(listeners);
		}

		for (DeploymentListener listener : copy)
			listener.deployApp(appId);
	}

	@Override
	public void process(WatchedEvent event)
	{

		if (event.getType() == EventType.None)
		{
			logger.debug("WatchApp - Connectino has changed");
			zooHelper.getQueue().insert(this);
			return;
		}

		try
		{
			ZooKeeper zk = zooHelper.getZooKeeper();

			// Geht the path to the node which data has changed
			String path = event.getPath();
			if (path == null)
			{
				logger.warn("Path is null");
				return;
			}

			// Extract the node data
			Stat stat = new Stat();
			byte[] data = zk.getData(path, false, stat);
			String appId = new String(data);

			// The ZooKeeper API cannot remove watches. We have to check here if
			// the app is still watched
			if (watching.contains(appId))
			{
				logger.debug("Deployed AppId: " + appId);
				appDeployed(appId);
			}

		} catch (InterruptedException e)
		{
			logger.error(e);
		} catch (IOException e)
		{
			logger.error(e);
		} catch (KeeperException e)
		{
			logger.error(e);
		} finally
		{
			// Reschedule this task
			zooHelper.getQueue().insert(this);
		}
	}

	@Override
	public boolean execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		logger.debug("WatchApp");

		ZooKeeper zk = zooHelper.getZooKeeper();
		String path = ZNodes.ZNODE_APPS;

		try
		{
			// Iterate over all watched apps
			for (String watch : this.watching)
			{
				// Assumend path to the node
				final String watchPath = path + "/" + watch;

				// Check if a node for the AppId exists
				Stat stat = zk.exists(watchPath, false);
				if (stat == null)
				{
					logger.error("Cannot watch AppId: " + watch);

					// Enqueue a register task before this task
					zooHelper.getQueue().insertBefore(this, new RegisterApp(watch));

					return false;
				}

				// Get the node data
				stat = new Stat();
				byte[] data = zk.getData(watchPath, false, stat);
				String childAppId = new String(data);

				// Check if we are watching this node
				if (watching.contains(childAppId))
				{
					logger.debug("Watching ZooKeeper node: " + watchPath);

					// Watch the node
					zk.exists(watchPath, this);
				}
			}

		} catch (KeeperException e)
		{
			logger.error(e);
			return false;
		} catch (InterruptedException e)
		{
			logger.error(e);
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

package org.prot.controller.zookeeper.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.data.Stat;
import org.prot.controller.zookeeper.DeploymentListener;
import org.prot.util.ObjectSerializer;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.JobState;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;
import org.prot.util.zookeeper.data.AppEntry;

public class WatchApp implements Job, Watcher
{
	private static final Logger logger = Logger.getLogger(WatchApp.class);

	private ZooHelper zooHelper;

	private Map<String, Integer> watching = new HashMap<String, Integer>();

	private List<DeploymentListener> listeners = new ArrayList<DeploymentListener>();

	public void addDeploymentListener(DeploymentListener listener)
	{
		synchronized (listeners)
		{
			listeners.add(listener);
		}
	}

	public synchronized void watchApp(String appId)
	{
		Integer value = null;
		if (watching.containsKey(appId))
			value = watching.get(appId);
		else
			value = 0;

		value++;
		watching.put(appId, value);
		zooHelper.getQueue().insert(this);
	}

	public synchronized void removeWatch(String appId)
	{
		if (watching.containsKey(appId))
		{
			Integer value = watching.get(appId);
			value--;
			if (value <= 0)
				watching.remove(appId);
			else
				watching.put(appId, value);
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
			listener.deployApp(appId);
	}

	@Override
	public void process(WatchedEvent event)
	{
		try
		{
			// Check the event type
			if (event.getType() == EventType.None)
				return;

			// Check the path
			final String path = event.getPath();
			if (path == null)
				return;

			// Connection to zookeeper
			ZooKeeper zk = zooHelper.getZooKeeper();

			// Extract the AppEntry object
			Stat stat = new Stat();
			byte[] data = zk.getData(path, false, stat);
			ObjectSerializer serializer = new ObjectSerializer();
			AppEntry entry = (AppEntry) serializer.deserialize(data);

			// The ZooKeeper API cannot remove watches. We have to check here if
			// the appId is still in the watchlist
			if (watching.containsKey(entry.appId))
				appDeployed(entry.appId);

		} catch (InterruptedException e)
		{
			logger.error("InterruptedException", e);
		} catch (KeeperException e)
		{
			logger.error("KeeperException", e);
		} finally
		{
			// Reschedule this task (install watchers)
			zooHelper.getQueue().insert(this);
		}
	}

	@Override
	public JobState execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		if (!zooHelper.checkConnection())
			return JobState.RETRY_LATER;

		ZooKeeper zk = zooHelper.getZooKeeper();

		try
		{
			// Iterate over all watched apps
			for (String watch : this.watching.keySet())
			{
				// Assumend path to the node
				final String watchPath = ZNodes.ZNODE_APPS + "/" + watch;

				// Check if a node for the AppId exists
				Stat stat = zk.exists(watchPath, false);
				if (stat == null)
				{
					// Register the application
					zooHelper.getQueue().insert(this, new RegisterApp(watch));

					// Retry
					return JobState.RETRY;
				}

				// We are watching this node
				stat = zk.exists(watchPath, this);
			}

			return JobState.OK;

		} catch (KeeperException e)
		{
			throw e;
		} catch (InterruptedException e)
		{
			logger.error(e);
			return JobState.RETRY_LATER;
		}
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

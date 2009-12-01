package org.prot.manager.zookeeper.jobs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.prot.manager.data.ControllerInfo;
import org.prot.manager.data.ControllerRegistry;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;
import org.prot.util.zookeeper.data.Controller;

public class LookupControllers implements Job, Watcher
{
	private final static Logger logger = Logger.getLogger(LookupControllers.class);

	private ZooHelper zooHelper;

	private ControllerRegistry registry;

	@Override
	public void process(WatchedEvent event)
	{
		// Reexecute this job
		zooHelper.getQueue().insert(this);
	}

	private Controller loadController(byte[] buffer) throws IOException
	{
		ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(buffer));
		try
		{
			return (Controller) oin.readObject();
		} catch (ClassNotFoundException e)
		{
			logger.error("Could not deserialize ZooKeeper data: " + e);
		}

		return null;
	}

	@Override
	public boolean execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		// ZooKeeper connection
		ZooKeeper zk = zooHelper.getZooKeeper();

		// List the controllers directory in ZooKeeper
		logger.debug("Searching Controllers in: " + ZNodes.ZNODE_CONTROLLER);
		List<String> childs = zk.getChildren(ZNodes.ZNODE_CONTROLLER, this);

		// Iterate over all found Controllers
		List<ControllerInfo> infos = new ArrayList<ControllerInfo>();
		for (String child : childs)
		{
			logger.debug("Found Controller: " + child);

			child = ZNodes.ZNODE_CONTROLLER + "/" + child;
			Stat stat = new Stat();
			byte[] data = zk.getData(child, false, stat);

			Controller controller = loadController(data);
			ControllerInfo info = new ControllerInfo();
			info.setAddress(controller.address);
			info.setServiceAddress(controller.serviceAddress);
			info.setPort(controller.port);
			infos.add(info);
		}

		logger.debug("Updating ControllerRegistry with " + infos.size() + " Controllers");
		registry.update(infos);

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

	public void setRegistry(ControllerRegistry registry)
	{
		this.registry = registry;
	}
}

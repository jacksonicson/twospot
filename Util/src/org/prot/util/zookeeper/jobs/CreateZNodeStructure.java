package org.prot.util.zookeeper.jobs;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.data.ACL;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;

public class CreateZNodeStructure implements Job
{
	@Override
	public boolean execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		ZooKeeper zooKeeper = zooHelper.getZooKeeper();
		List<ACL> acl = zooHelper.getACL();

		try
		{
			zooKeeper.create(ZNodes.ZNODE_APPS, new byte[] {}, acl, CreateMode.PERSISTENT);
		} catch (NodeExistsException e)
		{
			// Do nothing
		}

		try
		{
			zooKeeper.create(ZNodes.ZNODE_CONTROLLER, new byte[] {}, acl, CreateMode.PERSISTENT);
		} catch (NodeExistsException e)
		{
			// Do nothing
		}

		return true;
	}

	@Override
	public boolean isRetryable()
	{
		return false;
	}

	@Override
	public void init(ZooHelper zooHelper)
	{
	}
}

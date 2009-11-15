package org.prot.manager.zookeeper;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;

public class ManagementService
{
	private static final Logger logger = Logger.getLogger(ManagementService.class);

	// ZooKeeper helper
	private ZooHelper zooHelper;

	// Hostname of the master
	private String host;

	// Port of the master
	private int port;

	public void init()
	{
		try
		{
			registerMaster();
		} catch (KeeperException e)
		{
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		ControllerWatcher watcher = new ControllerWatcher(zooHelper); 
		zooHelper.addWatcher(watcher);
	}

	private void registerMaster() throws KeeperException, InterruptedException
	{
		logger.debug("Registering this master with the ZooKeeper");

		ZooKeeper zk = zooHelper.getZooKeeper();

		Stat statMaster = zk.exists(ZNodes.ZNODE_MASTER, false);
		if (statMaster == null)
		{
			byte[] address = (host + ":" + port).getBytes();
			zk.create(ZNodes.ZNODE_MASTER, address, zooHelper.getACL(), CreateMode.EPHEMERAL);
			statMaster = zk.exists(ZNodes.ZNODE_MASTER, true);
			
			logger.info("Master registered in ZooKeeper");
			return;
		} else
		{
			logger.error("ZooKeeper already contains a ZNode: " + ZNodes.ZNODE_MASTER
					+ ". Multimaster is not supported");
			System.exit(1);
		}
	}

	public void setZooHelper(ZooHelper zooHelper)
	{
		this.zooHelper = zooHelper;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public void setPort(int port)
	{
		this.port = port;
	}
}

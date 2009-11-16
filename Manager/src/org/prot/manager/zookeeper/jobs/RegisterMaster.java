package org.prot.manager.zookeeper.jobs;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;

public class RegisterMaster implements Job
{
	private static final Logger logger = Logger.getLogger(RegisterMaster.class);

	// Manager communication details
	private String host;
	private int port;

	public RegisterMaster(String host, int port)
	{
		this.host = host;
		this.port = port;
	}

	@Override
	public boolean execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
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
			return false;
		} else
		{
			logger.error("ZooKeeper already contains a ZNode: " + ZNodes.ZNODE_MASTER
					+ ". Multimaster is not supported");
			System.exit(1);
		}

		return false;
	}

	@Override
	public boolean isRetryable()
	{
		return true;
	}
}

package org.prot.manager.zookeeper.jobs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.prot.util.net.AddressExtractor;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.JobState;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;

public class RegisterMaster implements Job, Watcher
{
	private static final Logger logger = Logger.getLogger(RegisterMaster.class);

	private ZooHelper zooHelper;

	// Manager communication details
	private String networkInterface;
	private InetAddress address;
	private int port;

	public RegisterMaster(String networkInterface, int port)
	{
		this.networkInterface = networkInterface;
		this.port = port;
	}

	public void init()
	{
		try
		{
			address = AddressExtractor.getInetAddress(networkInterface, false);
			logger.info("Using inet-address: " + address);
		} catch (SocketException e)
		{
			logger.error("Could not determine network address", e);
			System.exit(1);
		}
	}

	@Override
	public void process(WatchedEvent event)
	{
		switch (event.getType())
		{
		case NodeDeleted:
		case None:
			zooHelper.getQueue().insert(this);
		}
	}

	@Override
	public JobState execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		logger.debug("Registering this master with the ZooKeeper");

		ZooKeeper zk = zooHelper.getZooKeeper();

		Stat statMaster = zk.exists(ZNodes.ZNODE_MASTER, false);
		if (statMaster == null)
		{
			byte[] address = (this.address.getHostAddress() + ":" + port).getBytes();
			zk.create(ZNodes.ZNODE_MASTER, address, zooHelper.getACL(), CreateMode.EPHEMERAL);
			statMaster = zk.exists(ZNodes.ZNODE_MASTER, true);

			logger.info("Master registered in ZooKeeper");
			return JobState.OK;
		} else
		{
			logger.warn("ZooKeeper already contains a ZNode: " + ZNodes.ZNODE_MASTER
					+ ". Multimaster is not supported");

			statMaster = zk.exists(ZNodes.ZNODE_MASTER, true);
			return JobState.OK;
		}
	}

	@Override
	public boolean isRetryable()
	{
		return true;
	}

	@Override
	public void init(ZooHelper zooHelper)
	{
		this.zooHelper = zooHelper;
	}
}

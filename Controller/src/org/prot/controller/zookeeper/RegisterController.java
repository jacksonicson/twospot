package org.prot.controller.zookeeper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.prot.controller.config.Configuration;
import org.prot.util.ObjectSerializer;
import org.prot.util.net.AddressExtractor;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;
import org.prot.util.zookeeper.data.ControllerEntry;

public class RegisterController implements Job
{
	private static final Logger logger = Logger.getLogger(RegisterController.class);

	private String networkInterface;

	public RegisterController(String networkInterface)
	{
		this.networkInterface = networkInterface;
	}

	private final InetAddress getAddress(String networkInterface)
	{
		try
		{
			return AddressExtractor.getInetAddress(networkInterface, false);
		} catch (SocketException e)
		{
			logger.fatal("Could not get IP address for network interface " + networkInterface, e);
			logger.fatal("Shutting down...");
			System.exit(1);
		}

		return null;
	}

	private final String generateUUID()
	{
		return java.util.UUID.randomUUID().toString();
	}

	@Override
	public boolean execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		ZooKeeper zk = zooHelper.getZooKeeper();
		String path = ZNodes.ZNODE_CONTROLLER + "/" + generateUUID();

		ControllerEntry entry = new ControllerEntry();
		entry.serviceAddress = getAddress(networkInterface).getHostAddress();
		entry.address = getAddress(networkInterface).getHostAddress();
		entry.port = Configuration.getConfiguration().getControllerPort();

		ObjectSerializer serializer = new ObjectSerializer();
		byte[] data = serializer.serialize(entry);

		if (zk.exists(path, false) != null)
		{
			logger.warn("Could not register within ZooKeeper. Registration path already exists: " + path);
			return false;
		}

		try
		{
			String createdPath = zk.create(path, data, zooHelper.getACL(), CreateMode.EPHEMERAL);
			logger.info("Controller registered within ZooKeeper: " + createdPath);
		} catch (KeeperException e)
		{
			logger.error("Could not register within ZooKeeper. Registration path already exists: " + path, e);
			return false;
		}

		return true;
	}

	@Override
	public void init(ZooHelper zooHelper)
	{
		// Do nothing
	}

	@Override
	public boolean isRetryable()
	{
		return true;
	}
}

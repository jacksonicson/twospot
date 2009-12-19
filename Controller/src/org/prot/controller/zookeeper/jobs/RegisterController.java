package org.prot.controller.zookeeper.jobs;

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

	@Override
	public boolean execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		ZooKeeper zk = zooHelper.getZooKeeper();
		String controllerPath = ZNodes.ZNODE_CONTROLLER + "/" + Configuration.getConfiguration().getUID();

		// Create a new ControllerEntry object which serialized version is saved
		// to the ZooKeeper
		ControllerEntry entry = new ControllerEntry();
		entry.serviceAddress = getAddress(networkInterface).getHostAddress();
		entry.address = getAddress(networkInterface).getHostAddress();
		entry.port = Configuration.getConfiguration().getControllerPort();

		// Serialize the ControllerEntry object
		ObjectSerializer serializer = new ObjectSerializer();
		byte[] entryData = serializer.serialize(entry);

		// Check if the ZooKeeper-Node for the Controller already exists
		if (zk.exists(controllerPath, false) != null)
		{
			logger.warn("Could not register within ZooKeeper - already exists: " + controllerPath);
			return false;
		}

		try
		{
			String createdPath = zk.create(controllerPath, entryData, zooHelper.getACL(),
					CreateMode.EPHEMERAL);
			logger.info("Controller ZooKeeper-Path: " + createdPath);

			return true;

		} catch (KeeperException e)
		{
			logger.error("Could not register within ZooKeeper: " + controllerPath, e);
			return false;
		}
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

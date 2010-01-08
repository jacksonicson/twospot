package org.prot.controller.zookeeper.jobs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.prot.controller.config.Configuration;
import org.prot.util.ObjectSerializer;
import org.prot.util.net.AddressExtractor;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;
import org.prot.util.zookeeper.data.ControllerEntry;

public class RegisterController implements Job, Watcher
{
	private static final Logger logger = Logger.getLogger(RegisterController.class);

	private String networkInterface;

	private ZooHelper zooHelper;

	private String controllerPath = null;

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
	public void process(WatchedEvent event)
	{
		switch (event.getType())
		{
		case NodeDeleted:
		case None:
			zooHelper.getQueue().insert(this);
			return;
		}
	}

	@Override
	public boolean execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		ZooKeeper zk = zooHelper.getZooKeeper();

		// Create a controller path if necessary
		if (controllerPath == null)
			controllerPath = ZNodes.ZNODE_CONTROLLER + "/" + Configuration.getConfiguration().getUID();

		// Create a new ControllerEntry object which serialized version is saved
		// to the ZooKeeper
		ControllerEntry entry = new ControllerEntry();
		entry.serviceAddress = getAddress(networkInterface).getHostAddress();
		entry.address = getAddress(networkInterface).getHostAddress();
		entry.port = Configuration.getConfiguration().getControllerPort();

		// Serialize the ControllerEntry object
		ObjectSerializer serializer = new ObjectSerializer();
		byte[] entryData = serializer.serialize(entry);

		// Try creating a new node
		try
		{
			// Created node
			String createdPath = zk.create(controllerPath, entryData, zooHelper.getACL(),
					CreateMode.EPHEMERAL);

			logger.info("Controller ZooKeeper-Path: " + createdPath);
		} catch (KeeperException e)
		{
			switch (e.code())
			{
			case NODEEXISTS:
				break;
			default:
				logger.error("KeeperException", e);
				return false;
			}
		}

		// Watch the node for changes
		zk.exists(controllerPath, this);
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

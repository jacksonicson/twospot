package org.prot.controller.zookeeper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.prot.util.net.AddressExtractor;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;
import org.prot.util.zookeeper.data.Controller;

public class Register implements Job
{
	private static final Logger logger = Logger.getLogger(Register.class);

	private String networkInterface;
	private String name;

	public Register(String networkInterface, String name)
	{
		this.networkInterface = networkInterface;
		this.name = name;
	}

	private InetAddress getAddress(String networkInterface)
	{
		try
		{
			return AddressExtractor.getInetAddress(networkInterface, false);
		} catch (SocketException e)
		{
			logger.error("Could not get IP address for network interface " + networkInterface, e);
			System.exit(1);
		}

		return null;
	}

	@Override
	public boolean execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		ZooKeeper zk = zooHelper.getZooKeeper();

		Controller controller = new Controller();
		controller.serviceAddress = "localhost";
		controller.address = getAddress(networkInterface).getHostAddress();
		controller.port = 8080;

		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bo);
		out.writeObject(controller);

		String path = zk.create(ZNodes.ZNODE_CONTROLLER + "/" + name, bo.toByteArray(), zooHelper.getACL(),
				CreateMode.EPHEMERAL);

		logger.info("Controller registered within ZooKeeper: " + path);

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

package org.prot.controller.zookeeper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;
import org.prot.util.zookeeper.data.Controller;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ManagementService
{
	private static final Logger logger = Logger.getLogger(ManagementService.class);

	// ZooKeeper helper
	private ZooHelper zooHelper;

	private String host;

	private String name;

	public void init()
	{
		try
		{
			register();
		} catch (KeeperException e)
		{
			logger.error("Could not register", e);
			System.exit(1);
		} catch (InterruptedException e)
		{
			logger.error("Could not register", e);
			System.exit(1);
		}
	}

	public void register() throws KeeperException, InterruptedException
	{
		ZooKeeper zk = zooHelper.getZooKeeper();

		try
		{
			Controller controller = new Controller();
			controller.address = "http://localhost";
			controller.port = 8080;
			ByteArrayOutputStream bo = new ByteArrayOutputStream(); 
			ObjectOutputStream out = new ObjectOutputStream(bo);
			
			out.writeObject(controller); 
			
			String path = zk.create(ZNodes.ZNODE_CONTROLLER + "/" + name, bo.toByteArray(),
					zooHelper.getACL(), CreateMode.EPHEMERAL);

			logger.info("Controller registered at ZooKeeper with: " + path);

		} catch (NodeExistsException e)
		{
			logger.error("Controller already registered in ZooKeeper", e);
			System.exit(1);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void registerApp(String appId)
	{
		throw new NotImplementedException();
	}

	public void deleteApp(String appId)
	{
		throw new NotImplementedException();
	}

	public void setZooHelper(ZooHelper zooHelper)
	{
		this.zooHelper = zooHelper;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}

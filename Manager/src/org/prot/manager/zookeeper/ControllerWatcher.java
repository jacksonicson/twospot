package org.prot.manager.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.prot.manager.data.ControllerRegistry;
import org.prot.util.zookeeper.FilteredWatcher;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;

public class ControllerWatcher implements FilteredWatcher
{
	private ZooHelper zooHelper;

	private ControllerRegistry registry;

	public ControllerWatcher(ZooHelper zooHelper)
	{
		this.zooHelper = zooHelper;
		register();
	}

	void register()
	{
//		ZooKeeper zk = zooHelper.getZooKeeper();
//		try
//		{
//			zk.getChildren(ZNodes.ZNODE_CONTROLLER, true);
//		} catch (KeeperException e)
//		{
//			e.printStackTrace();
//		} catch (InterruptedException e)
//		{
//			e.printStackTrace();
//		}
	}
	
	void update()
	{
//		ZooKeeper zk = zooHelper.getZooKeeper();
//		try
//		{
//			List<String> childs = zk.getChildren(ZNodes.ZNODE_CONTROLLER, true);
//			List<ControllerInfo> info = new ArrayList<ControllerInfo>();
//			for(String child : childs)
//			{
//				System.out.println("Child: " + child); 
//				
//				child = ZNodes.ZNODE_CONTROLLER + "/" + child; 
//				
//				Stat stat = new Stat();
//				byte[] data = zk.getData(child, false, stat); 
//				ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
//				Object o = (Object)in.readObject();
//				if(o instanceof Controller)
//				{
//					Controller c = (Controller)o;
//					ControllerInfo ci = new ControllerInfo();
//					ci.setAddress(c.address);
//					ci.setPort(c.port);
//					info.add(ci); 
//					System.out.println("DONE: " + c.address + " / " + c.port); 
//					
//				} else
//				{
//					System.out.println("Unable to cast");
//				}
//			}
//			
//			System.out.println("Updating"); 
//			// registry.update(info); 
//			
//		} catch (KeeperException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InterruptedException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e)
//		{
//			e.printStackTrace();
//		} catch (ClassNotFoundException e)
//		{
//			e.printStackTrace();
//		}
	}

	@Override
	public boolean matches(WatchedEvent e)
	{
		return e.getPath().equals(ZNodes.ZNODE_CONTROLLER);
	}

	@Override
	public void process(WatchedEvent event)
	{
		switch (event.getType())
		{
		case NodeChildrenChanged:
			update(); 
			break;
		}
	}
}

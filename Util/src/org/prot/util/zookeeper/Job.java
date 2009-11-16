package org.prot.util.zookeeper;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

public interface Job
{
	public void init(ZooHelper zooHelper);
	
	public boolean execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException;
	
	public boolean isRetryable();
}

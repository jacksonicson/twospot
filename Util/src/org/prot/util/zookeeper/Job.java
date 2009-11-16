package org.prot.util.zookeeper;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

public interface Job
{
	public boolean execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException;
	
	public boolean isRetryable();
}

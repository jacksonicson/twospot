package org.prot.util.zookeeper;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

/**
 * 
 * Every ZooKeeper job has to implement this interface
 * 
 * @author Andreas Wolke
 * 
 */
public interface Job {

	public void init(ZooHelper zooHelper);

	public JobState execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException;

	public boolean isRetryable();
}

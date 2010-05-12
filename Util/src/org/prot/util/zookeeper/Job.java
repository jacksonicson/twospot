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

	/**
	 * called to initialize the job
	 * 
	 * @param zooHelper
	 */
	public void init(ZooHelper zooHelper);

	/**
	 * Implementation for the job goes here
	 * 
	 * @param zooHelper
	 * @return a job state
	 * @throws KeeperException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public JobState execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException;

	/**
	 * @return true if this job is retrieable after an the occurance of an error
	 */
	public boolean isRetryable();
}

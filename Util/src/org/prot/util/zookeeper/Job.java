/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
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

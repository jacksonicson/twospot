package org.prot.util.zookeeper.jobs;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.ACL;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.JobState;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;

public class CreateZNodeStructure implements Job {
	private static final Logger logger = Logger.getLogger(CreateZNodeStructure.class);

	@Override
	public JobState execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException {
		ZooKeeper zooKeeper = zooHelper.getZooKeeper();
		List<ACL> acl = zooHelper.getACL();

		try {
			// Creaets the apps node
			zooKeeper.create(ZNodes.ZNODE_APPS, new byte[] {}, acl, CreateMode.PERSISTENT);
		} catch (KeeperException e) {
			if (e.code() != Code.NODEEXISTS)
				logger.error("KeeperException", e);
		}

		try {
			// Creates the controllers node
			zooKeeper.create(ZNodes.ZNODE_CONTROLLER, new byte[] {}, acl, CreateMode.PERSISTENT);
		} catch (KeeperException e) {
			if (e.code() != Code.NODEEXISTS)
				logger.error("KeeperException", e);
		}

		return JobState.OK;
	}

	@Override
	public boolean isRetryable() {
		return false;
	}

	@Override
	public void init(ZooHelper zooHelper) {
	}
}

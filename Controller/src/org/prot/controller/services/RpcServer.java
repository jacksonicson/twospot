package org.prot.controller.services;

import java.io.IOException;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.prot.controller.app.TokenChecker;
import org.prot.controller.services.db.DbServiceImpl;
import org.prot.controller.services.db.HbaseDbDao;
import org.prot.controller.services.deploy.DeployServiceImpl;
import org.prot.controller.services.log.LogServiceImpl;
import org.prot.controller.services.user.UserServiceImpl;
import org.prot.controller.zookeeper.SynchronizationService;

import com.googlecode.protobuf.socketrpc.SocketRpcServer;

public class RpcServer {

	private static final Logger logger = Logger.getLogger(RpcServer.class);

	private TokenChecker tokenChecker;
	private SynchronizationService synchronizationService;
	private HbaseDbDao dbDao;

	public void init() {
		Thread thr = new Thread() {
			public void run() {
				SocketRpcServer socketRpcServer = new SocketRpcServer(6548, Executors.newFixedThreadPool(1));
				socketRpcServer.registerService(new UserServiceImpl());
				socketRpcServer.registerService(new LogServiceImpl());

				DbServiceImpl dbs = new DbServiceImpl();
				dbs.setTokenChecker(tokenChecker);
				dbs.setDbDao(dbDao);

				socketRpcServer.registerService(dbs);

				DeployServiceImpl ds = new DeployServiceImpl();
				ds.setTokenChecker(tokenChecker);
				ds.setManagementService(synchronizationService);

				socketRpcServer.registerService(ds);
				try {
					socketRpcServer.run();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		thr.start();
	}

	public void setTokenChecker(TokenChecker tokenChecker) {
		this.tokenChecker = tokenChecker;
	}

	public void setSynchronizationService(SynchronizationService synchronizationService) {
		this.synchronizationService = synchronizationService;
	}

	public void setDbDao(HbaseDbDao dbDao) {
		this.dbDao = dbDao;
	}
}

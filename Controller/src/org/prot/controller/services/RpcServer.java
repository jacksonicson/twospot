package org.prot.controller.services;

import java.io.IOException;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.prot.controller.app.TokenChecker;
import org.prot.controller.services.deploy.DeployServiceImpl;
import org.prot.controller.services.gen.Services.DbService;
import org.prot.controller.services.gen.Services.FetchTable;
import org.prot.controller.services.gen.Services.TableData;
import org.prot.controller.services.gen.Services.TableList;
import org.prot.controller.services.log.LogServiceImpl;
import org.prot.controller.services.user.UserServiceImpl;
import org.prot.controller.zookeeper.SynchronizationService;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.socketrpc.SocketRpcServer;

public class RpcServer {

	private static final Logger logger = Logger.getLogger(RpcServer.class);

	private TokenChecker tokenChecker; 
	private SynchronizationService synchronizationService; 
	
	class DbServiceImpl extends DbService {

		@Override
		public void getTableData(RpcController controller, FetchTable request, RpcCallback<TableData> done) {
			// TODO Auto-generated method stub

		}

		@Override
		public void getTables(RpcController controller, FetchTable request, RpcCallback<TableList> done) {
			// TODO Auto-generated method stub

		}
	}

	public RpcServer() {
		
	}
	
	
	public void init() {
		Thread thr = new Thread() {
			public void run() {
				SocketRpcServer socketRpcServer = new SocketRpcServer(6548, Executors.newFixedThreadPool(1));
				socketRpcServer.registerService(new UserServiceImpl());
				socketRpcServer.registerService(new LogServiceImpl());
				
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
	
}

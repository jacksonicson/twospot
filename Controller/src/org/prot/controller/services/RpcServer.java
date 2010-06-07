package org.prot.controller.services;

import java.io.IOException;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.prot.controller.services.gen.Services.DbService;
import org.prot.controller.services.gen.Services.FetchTable;
import org.prot.controller.services.gen.Services.TableData;
import org.prot.controller.services.gen.Services.TableList;
import org.prot.controller.services.log.LogServiceImpl;
import org.prot.controller.services.user.UserServiceImpl;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.socketrpc.SocketRpcServer;

public class RpcServer {

	private static final Logger logger = Logger.getLogger(RpcServer.class);

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
		Thread thr = new Thread() {
			public void run() {
				SocketRpcServer socketRpcServer = new SocketRpcServer(6548, Executors.newFixedThreadPool(1));
				socketRpcServer.registerService(new UserServiceImpl());
				socketRpcServer.registerService(new LogServiceImpl());
				socketRpcServer.registerService(new DbServiceImpl());
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
}

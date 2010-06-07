package org.prot.app.services.db;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.prot.app.services.user.UserService;
import org.prot.app.services.user.UserServiceFactory;
import org.prot.appserver.config.Configuration;
import org.prot.controller.services.gen.Services.FetchTable;
import org.prot.controller.services.gen.Services.TableData;
import org.prot.controller.services.gen.Services.TableList;
import org.prot.jdo.storage.messages.EntityMessage;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.RpcCallback;
import com.googlecode.protobuf.socketrpc.SocketRpcChannel;
import com.googlecode.protobuf.socketrpc.SocketRpcController;

public final class DbBrowserService {
	private static final Logger logger = Logger.getLogger(DbBrowserService.class);

	private UserService userService;

	private UserService getUserService() {
		if (this.userService == null)
			this.userService = UserServiceFactory.getUserService();

		return this.userService;
	}

	public List<String> getTables(final String appId) {

		String user = getUserService().getCurrentUser();
		if (user == null) {
			logger.debug("User must be logged in to browse tables");
			return null;
		}

		final String token = Configuration.getInstance().getAuthenticationToken();

		return AccessController.doPrivileged(new PrivilegedAction<List<String>>() {

			List<String> names;

			@Override
			public List<String> run() {
				SocketRpcChannel socketRpcChannel = new SocketRpcChannel("localhost", 6548);
				SocketRpcController rpcController = socketRpcChannel.newRpcController();

				org.prot.controller.services.gen.Services.DbService stub = org.prot.controller.services.gen.Services.DbService
						.newStub(socketRpcChannel);

				FetchTable.Builder builder = FetchTable.newBuilder();
				builder.setAppId(appId);
				builder.setToken(token);
				builder.setKind("null");

				stub.getTables(rpcController, builder.build(), new RpcCallback<TableList>() {
					@Override
					public void run(TableList result) {
						names = result.getTableNamesList();
					}
				});

				return names;
			}
		});
	}

	public List<EntityMessage> getTableData(final String appId, final String kind) {
		String user = getUserService().getCurrentUser();
		if (user == null) {
			logger.debug("User msut be logged in to browse tables");
			return null;
		}

		final String token = Configuration.getInstance().getAuthenticationToken();

		List<byte[]> result = AccessController.doPrivileged(new PrivilegedAction<List<byte[]>>() {

			List<byte[]> data;

			@Override
			public List<byte[]> run() {
				SocketRpcChannel socketRpcChannel = new SocketRpcChannel("localhost", 6548);
				SocketRpcController rpcController = socketRpcChannel.newRpcController();

				org.prot.controller.services.gen.Services.DbService stub = org.prot.controller.services.gen.Services.DbService
						.newStub(socketRpcChannel);

				FetchTable.Builder builder = FetchTable.newBuilder();
				builder.setAppId(appId);
				builder.setKind(kind);
				builder.setToken(token);

				stub.getTableData(rpcController, builder.build(), new RpcCallback<TableData>() {

					@Override
					public void run(TableData arg0) {
						data = new ArrayList<byte[]>();
						for (ByteString bs : arg0.getTableDataList())
							data.add(bs.toByteArray());
					}
				});

				return data;
			}
		});

		List<EntityMessage> entityMessages = new ArrayList<EntityMessage>();

		for (byte[] entity : result) {
			EntityMessage.Builder builder = EntityMessage.newBuilder();
			try {
				builder.mergeFrom(entity);
				EntityMessage entityMsg = builder.build();
				entityMessages.add(entityMsg);

			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
			}
		}

		logger.debug("Builded: " + entityMessages.size());

		return entityMessages;
	}
}

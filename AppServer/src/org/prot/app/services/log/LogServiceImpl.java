package org.prot.app.services.log;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.prot.appserver.config.Configuration;
import org.prot.controller.services.gen.Services.ListMessages;
import org.prot.controller.services.gen.Services.MessageList;
import org.prot.controller.services.gen.Services.Void;

import com.google.protobuf.RpcCallback;
import com.googlecode.protobuf.socketrpc.SocketRpcChannel;
import com.googlecode.protobuf.socketrpc.SocketRpcController;

public final class LogServiceImpl implements LogService {
	private static final Logger logger = Logger.getLogger(LogServiceImpl.class);

	private void log(final String message, final int severity) {
		final String token = Configuration.getInstance().getAuthenticationToken();
		final String appId = Configuration.getInstance().getAppId();

		AccessController.doPrivileged(new PrivilegedAction<Void>() {
			private String retUrl;

			@Override
			public Void run() {
				SocketRpcChannel socketRpcChannel = new SocketRpcChannel("localhost", 6548);
				SocketRpcController rpcController = socketRpcChannel.newRpcController();

				org.prot.controller.services.gen.Services.LogService stub = org.prot.controller.services.gen.Services.LogService
						.newStub(socketRpcChannel);

				org.prot.controller.services.gen.Services.LogMessage.Builder build = org.prot.controller.services.gen.Services.LogMessage
						.newBuilder();
				build.setAppid(appId);
				build.setSeverity(severity);
				build.setMessage(message);
				build.setToken(token);

				stub.log(rpcController, build.build(), new RpcCallback<Void>() {
					@Override
					public void run(Void arg0) {
					}
				});

				return null;
			}
		});
	}

	public List<LogMessage> getMessages(final String appId, final int severity) {

		final String token = Configuration.getInstance().getAuthenticationToken();

		ArrayList<LogMessage> msgs = AccessController
				.doPrivileged(new PrivilegedAction<ArrayList<LogMessage>>() {
					private ArrayList<LogMessage> result;

					@Override
					public ArrayList<LogMessage> run() {
						SocketRpcChannel socketRpcChannel = new SocketRpcChannel("localhost", 6548);
						SocketRpcController rpcController = socketRpcChannel.newRpcController();

						org.prot.controller.services.gen.Services.LogService stub = org.prot.controller.services.gen.Services.LogService
								.newStub(socketRpcChannel);

						ListMessages.Builder build = ListMessages.newBuilder();
						build.setAppId(appId);
						build.setSeverity(severity);
						build.setToken(token);

						stub.listMessages(rpcController, build.build(), new RpcCallback<MessageList>() {
							@Override
							public void run(MessageList list) {
								result = new ArrayList<LogMessage>();
								for (org.prot.controller.services.gen.Services.LogMessage msg : list
										.getMessagesList()) {
									LogMessage newMessage = new LogMessage();
									newMessage.setMessage(msg.getMessage());
									newMessage.setSeverity(msg.getSeverity());
									result.add(newMessage);
								}
							}
						});

						return result;
					}
				});

		return msgs;
	}

	public void debug(String message) {
		log(message, 0);
	}

	public void info(String message) {
		log(message, 1);
	}

	public void warn(String message) {
		log(message, 2);
	}

	public void error(String message) {
		log(message, 3);
	}
}

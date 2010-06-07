package org.prot.app.services.platform;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.prot.app.services.PrivilegedServiceException;
import org.prot.appserver.config.Configuration;
import org.prot.controller.services.gen.Services.AnnounceDeployment;
import org.prot.controller.services.gen.Services.AppDeployed;
import org.prot.controller.services.gen.Services.RegisterDeployment;
import org.prot.controller.services.gen.Services.Void;

import com.google.protobuf.RpcCallback;
import com.googlecode.protobuf.socketrpc.SocketRpcChannel;
import com.googlecode.protobuf.socketrpc.SocketRpcController;

public final class PlatformService {
	public String announceApp(final String appId, final String version) {
		final String token = Configuration.getInstance().getAuthenticationToken();
		if (token == null)
			throw new PrivilegedServiceException();

		return AccessController.doPrivileged(new PrivilegedAction<String>() {
			private String deployToken;

			@Override
			public String run() {
				SocketRpcChannel socketRpcChannel = new SocketRpcChannel("localhost", 6548);
				SocketRpcController rpcController = socketRpcChannel.newRpcController();

				org.prot.controller.services.gen.Services.DeployService stub = org.prot.controller.services.gen.Services.DeployService
						.newStub(socketRpcChannel);

				AnnounceDeployment.Builder builder = AnnounceDeployment.newBuilder();
				builder.setAppId(appId);
				builder.setToken(token);

				stub.announceDeploy(rpcController, builder.build(),
						new RpcCallback<org.prot.controller.services.gen.Services.String>() {
							@Override
							public void run(org.prot.controller.services.gen.Services.String ret) {
								deployToken = ret.getValue();
							}
						});

				return deployToken;
			}
		});
	}

	public void appDeployed(final String appId, final String version) {
		final String token = Configuration.getInstance().getAuthenticationToken();
		if (token == null)
			throw new PrivilegedServiceException();

		AccessController.doPrivileged(new PrivilegedAction<String>() {

			@Override
			public String run() {
				SocketRpcChannel socketRpcChannel = new SocketRpcChannel("localhost", 6548);
				SocketRpcController rpcController = socketRpcChannel.newRpcController();

				org.prot.controller.services.gen.Services.DeployService stub = org.prot.controller.services.gen.Services.DeployService
						.newStub(socketRpcChannel);

				AppDeployed.Builder builder = AppDeployed.newBuilder();
				builder.setAppId(appId);
				builder.setToken(token);
				builder.setVersion(version);

				stub.appDeployed(rpcController, builder.build(), new RpcCallback<Void>() {
					@Override
					public void run(Void arg0) {

					}
				});

				return null;
			}
		});
	}

	public boolean register(final String appId, final String version) {
		final String token = Configuration.getInstance().getAuthenticationToken();
		if (token == null)
			throw new PrivilegedServiceException();

		return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {

			Boolean result = true;

			@Override
			public Boolean run() {
				SocketRpcChannel socketRpcChannel = new SocketRpcChannel("localhost", 6548);
				SocketRpcController rpcController = socketRpcChannel.newRpcController();

				org.prot.controller.services.gen.Services.DeployService stub = org.prot.controller.services.gen.Services.DeployService
						.newStub(socketRpcChannel);

				RegisterDeployment.Builder builder = RegisterDeployment.newBuilder();
				builder.setAppId(appId);
				builder.setVersion("null");
				builder.setToken(token);

				stub.register(rpcController, builder.build(),
						new RpcCallback<org.prot.controller.services.gen.Services.Boolean>() {
							@Override
							public void run(org.prot.controller.services.gen.Services.Boolean success) {
								result = true;
							}
						});

				return result;
			}
		});
	}
}

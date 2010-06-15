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
package org.prot.app.services.platform;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.prot.app.services.ChannelRegistry;
import org.prot.app.services.PrivilegedServiceException;
import org.prot.appserver.config.Configuration;
import org.prot.controller.services.gen.Services.AnnounceDeployment;
import org.prot.controller.services.gen.Services.AppDeployed;
import org.prot.controller.services.gen.Services.RegisterDeployment;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcChannel;
import com.google.protobuf.RpcController;

public final class PlatformService {
	public String announceApp(final String appId, final String version) {
		final String token = Configuration.getInstance().getAuthenticationToken();
		if (token == null)
			throw new PrivilegedServiceException();

		return AccessController.doPrivileged(new PrivilegedAction<String>() {
			private String deployToken;

			@Override
			public String run() {
				ChannelRegistry registry = ChannelRegistry.getInstance();
				RpcChannel channel = registry.getChannel();
				RpcController controller = registry.getController(channel);

				org.prot.controller.services.gen.Services.DeployService stub = org.prot.controller.services.gen.Services.DeployService
						.newStub(channel);

				AnnounceDeployment.Builder builder = AnnounceDeployment.newBuilder();
				builder.setAppId(appId);
				builder.setToken(token);

				stub.announceDeploy(controller, builder.build(),
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
				ChannelRegistry registry = ChannelRegistry.getInstance();
				RpcChannel channel = registry.getChannel();
				RpcController controller = registry.getController(channel);

				org.prot.controller.services.gen.Services.DeployService stub = org.prot.controller.services.gen.Services.DeployService
						.newStub(channel);

				AppDeployed.Builder builder = AppDeployed.newBuilder();
				builder.setAppId(appId);
				builder.setToken(token);
				builder.setVersion(version);

				stub.appDeployed(controller, builder.build(), null);
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
				ChannelRegistry registry = ChannelRegistry.getInstance();
				RpcChannel channel = registry.getChannel();
				RpcController controller = registry.getController(channel);

				org.prot.controller.services.gen.Services.DeployService stub = org.prot.controller.services.gen.Services.DeployService
						.newStub(channel);

				RegisterDeployment.Builder builder = RegisterDeployment.newBuilder();
				builder.setAppId(appId);
				builder.setVersion("null");
				builder.setToken(token);

				stub.register(controller, builder.build(),
						new RpcCallback<org.prot.controller.services.gen.Services.Boolean>() {
							@Override
							public void run(org.prot.controller.services.gen.Services.Boolean success) {
								result = success.getValue();
							}
						});

				return result;
			}
		});
	}
}

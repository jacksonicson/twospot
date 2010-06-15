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
package org.prot.app.services.db;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.prot.app.services.ChannelRegistry;
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
import com.google.protobuf.RpcChannel;
import com.google.protobuf.RpcController;

public final class DbBrowserService {
	private static final Logger logger = Logger.getLogger(DbBrowserService.class);

	private UserService userService;

	private UserService getUserService() {
		if (this.userService == null)
			this.userService = UserServiceFactory.getUserService();

		return this.userService;
	}

	public List<String> getTables(final String appId) {
		final String token = Configuration.getInstance().getAuthenticationToken();

		String user = getUserService().getCurrentUser();
		if (user == null) {
			logger.debug("User must be logged in to browse tables");
			return null;
		}

		List<String> tableNames = AccessController.doPrivileged(new PrivilegedAction<List<String>>() {

			private List<String> tableNames;

			@Override
			public List<String> run() {
				ChannelRegistry registry = ChannelRegistry.getInstance();
				RpcChannel channel = registry.getChannel();
				RpcController controller = registry.getController(channel);

				org.prot.controller.services.gen.Services.DbService stub = org.prot.controller.services.gen.Services.DbService
						.newStub(channel);

				FetchTable.Builder builder = FetchTable.newBuilder();
				builder.setAppId(appId);
				builder.setToken(token);

				stub.getTables(controller, builder.build(), new RpcCallback<TableList>() {
					@Override
					public void run(TableList result) {
						tableNames = result.getTableNamesList();
					}
				});

				return tableNames;
			}
		});

		return tableNames;
	}

	public List<EntityMessage> getTableData(final String appId, final String kind) {
		final String token = Configuration.getInstance().getAuthenticationToken();

		String user = getUserService().getCurrentUser();
		if (user == null) {
			logger.debug("User msut be logged in to browse tables");
			return null;
		}

		List<byte[]> result = AccessController.doPrivileged(new PrivilegedAction<List<byte[]>>() {

			private List<byte[]> data;

			@Override
			public List<byte[]> run() {
				ChannelRegistry registry = ChannelRegistry.getInstance();
				RpcChannel channel = registry.getChannel();
				RpcController controller = registry.getController(channel);

				org.prot.controller.services.gen.Services.DbService stub = org.prot.controller.services.gen.Services.DbService
						.newStub(channel);

				FetchTable.Builder builder = FetchTable.newBuilder();
				builder.setAppId(appId);
				builder.setKind(kind);
				builder.setToken(token);

				stub.getTableData(controller, builder.build(), new RpcCallback<TableData>() {

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

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
package org.prot.app.services.log;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.prot.app.services.ChannelRegistry;
import org.prot.appserver.config.Configuration;
import org.prot.controller.services.gen.Services.ListMessages;
import org.prot.controller.services.gen.Services.MessageList;
import org.prot.controller.services.gen.Services.Void;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcChannel;
import com.google.protobuf.RpcController;

public final class LogServiceImpl implements LogService {
	private static final Logger logger = Logger.getLogger(LogServiceImpl.class);

	private void log(final String message, final int severity) {
		final String appId = Configuration.getInstance().getAppId();

		AccessController.doPrivileged(new PrivilegedAction<Void>() {

			@Override
			public Void run() {
				ChannelRegistry registry = ChannelRegistry.getInstance();
				RpcChannel channel = registry.getChannel();
				RpcController controller = registry.getController(channel);

				org.prot.controller.services.gen.Services.LogService stub = org.prot.controller.services.gen.Services.LogService
						.newStub(channel);

				org.prot.controller.services.gen.Services.LogMessage.Builder build = org.prot.controller.services.gen.Services.LogMessage
						.newBuilder();
				build.setAppid(appId);
				build.setSeverity(severity);
				build.setMessage(message);

				stub.log(controller, build.build(), null);

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
						ChannelRegistry registry = ChannelRegistry.getInstance();
						RpcChannel channel = registry.getChannel();
						RpcController controller = registry.getController(channel);

						org.prot.controller.services.gen.Services.LogService stub = org.prot.controller.services.gen.Services.LogService
								.newStub(channel);

						ListMessages.Builder build = ListMessages.newBuilder();
						build.setAppId(appId);
						build.setSeverity(severity);
						build.setToken(token);

						stub.listMessages(controller, build.build(), new RpcCallback<MessageList>() {
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

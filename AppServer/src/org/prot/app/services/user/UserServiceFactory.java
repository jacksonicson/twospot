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
package org.prot.app.services.user;

import org.apache.log4j.Logger;
import org.prot.appserver.config.Configuration;
import org.prot.controller.services.gen.Services.UserService.Stub;

import com.googlecode.protobuf.socketrpc.SocketRpcChannel;
import com.googlecode.protobuf.socketrpc.SocketRpcController;

public class UserServiceFactory {
	private static final Logger logger = Logger.getLogger(UserServiceFactory.class);

	private static final String CONTROLLER_ADDRESS = "localhost";

	private static UserService userService;

	private static final int getRmiPort() {
		return Configuration.getInstance().getRmiRegistryPort();
	}

	private static UserService createUserService() {
		return new UserServiceImpl(); 
	}

	private static UserService createMockUserService() {
		return new MockUserService();
	}

	public static UserService getUserService() {
		if (userService == null) {
			logger.debug("Creating new UserService");

			switch (Configuration.getInstance().getServerMode()) {
			case DEVELOPMENT:
				userService = createMockUserService();
				break;
			case SERVER:
				userService = createUserService();
				break;
			}

		}

		return userService;
	}
}

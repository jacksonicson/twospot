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
package org.prot.portal.login;

import org.apache.log4j.Logger;
import org.prot.portal.login.data.PlatformUser;
import org.prot.portal.services.UserService;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class RegisterHandlerController extends SimpleFormController
{
	private static final Logger logger = Logger.getLogger(RegisterHandlerController.class);

	private UserService userService;

	public RegisterHandlerController()
	{
		setCommandClass(RegisterCommand.class);
		setCommandName("registerCommand");
	}

	protected void doSubmitAction(Object command) throws Exception
	{
		RegisterCommand registerCommand = (RegisterCommand) command;
		PlatformUser user = registerCommand.clone(); 
		userService.registerUser(user, registerCommand.getPassword0());
	}

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}
}

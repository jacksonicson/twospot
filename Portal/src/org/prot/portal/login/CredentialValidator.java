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
import org.prot.portal.services.UserService;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class CredentialValidator implements Validator
{
	private static Logger logger = Logger.getLogger(CredentialValidator.class);

	private UserService userService;

	@Override
	public boolean supports(Class cls)
	{
		return cls.equals(LoginCommand.class);
	}

	@Override
	public void validate(Object command, Errors errors)
	{
		LoginCommand loginCommand = (LoginCommand) command;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "username",
				"Username must not be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password",
				"Password must not be empty");

		// Check credentials (only if no erros until now)
		if (errors.getAllErrors().size() == 0)
		{
			String username = loginCommand.getUsername().toLowerCase();
			String password = loginCommand.getPassword();

			boolean check = userService.checkCredentials(username, password);
			if (check == false)
			{
				errors.reject("", "Login failed - invalid credentials");
			}
		}
	}

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}
}

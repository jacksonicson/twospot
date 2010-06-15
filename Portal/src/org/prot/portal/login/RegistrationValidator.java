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

public class RegistrationValidator implements Validator
{
	private static Logger logger = Logger.getLogger(RegistrationValidator.class);

	private UserService userService;

	@Override
	public boolean supports(Class cls)
	{
		return cls.equals(RegisterCommand.class);
	}

	@Override
	public void validate(Object command, Errors errors)
	{
		RegisterCommand registerCommand = (RegisterCommand) command;

		// Check empty fileds
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "", "username is required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password0", "", "password is required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password1", "", "password is required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "", "e-mail is required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "surname", "", "surname is required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "forename", "", "forename is required");

		// Both passwords must be equal
		if (registerCommand.getPassword0().equals(registerCommand.getPassword1()) == false)
			errors.rejectValue("password1", "", "confirmed password does not match password");

		// Check if user already esists (only if no errors until now)
		if (!errors.hasErrors())
		{
			boolean exists = userService.existsUserId(registerCommand.getUsername());
			if (exists == true)
			{
				errors.rejectValue("username", "", "Username already exists");
			}
		}
	}

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}
}

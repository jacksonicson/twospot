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
		logger.info("checking credentials now"); 
		if (errors.getAllErrors().size() == 0)
		{
			logger.info("yes"); 
			String username = loginCommand.getUsername();
			String password = loginCommand.getPassword();

			boolean check = userService.checkCredentials(username, password);
			if (check == false)
			{
				logger.info("Login failed"); 
				errors.reject("", "Login failed - invalid credentials");
			}
		}
	}

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}
}

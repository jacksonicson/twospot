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
		RegisterCommand registerCommand = (RegisterCommand)command;

		// Check empty fileds
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "", "username is required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "md5Password", "", "password is required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "", "e-mail is required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "surname", "", "surname is required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "forename", "", "forename is required");
		
		// Check if user already esists (only if no errors until now)
		if(!errors.hasErrors())
		{
			boolean exists = userService.existsUserId(registerCommand.getUsername());
			if(exists == true)
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

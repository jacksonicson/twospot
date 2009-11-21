package org.prot.portal.login;

import org.apache.log4j.Logger;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class CredentialValidator implements Validator
{
	private static Logger logger = Logger.getLogger(CredentialValidator.class);
	
	@Override
	public boolean supports(Class cls)
	{
		return cls.equals(LoginCommand.class);
	}

	@Override
	public void validate(Object command, Errors errors)
	{
		LoginCommand loginCommand = (LoginCommand)command;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "username", "Username must not be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password", "Password must not be empty");
	}
}

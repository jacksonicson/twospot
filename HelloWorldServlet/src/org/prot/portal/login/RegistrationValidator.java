package org.prot.portal.login;

import org.apache.log4j.Logger;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class RegistrationValidator implements Validator
{
	private static Logger logger = Logger.getLogger(RegistrationValidator.class);
	
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
		
		// TODO: More validation
	}
}

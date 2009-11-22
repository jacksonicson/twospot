package org.prot.portal;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class RegisterAppValidator implements Validator
{

	@Override
	public boolean supports(Class clazz)
	{
		return clazz.equals(RegisterAppCommand.class);
	}

	@Override
	public void validate(Object target, Errors errors)
	{
		ValidationUtils.rejectIfEmpty(errors, "appId", "", "AppId required");
	}

}

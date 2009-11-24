package org.prot.portal.portal;

import java.util.Set;

import org.prot.app.services.UserServiceFactory;
import org.prot.portal.login.data.PlatformUser;
import org.prot.portal.services.AppService;
import org.prot.portal.services.UserService;
import org.prot.util.ReservedAppIds;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class RegisterAppValidator implements Validator
{
	private AppService appService;

	private UserService userService;

	@Override
	public boolean supports(Class clazz)
	{
		return clazz.equals(RegisterAppCommand.class);
	}

	@Override
	public void validate(Object target, Errors errors)
	{
		RegisterAppCommand registeredApp = (RegisterAppCommand) target;

		ValidationUtils.rejectIfEmpty(errors, "appId", "", "AppId required");

		// Do some further checks (if no errors until now)
		if (!errors.hasErrors())
		{
			// Check if appId is valid
			boolean isValid = ReservedAppIds.validateNewAppId(registeredApp.getAppId());
			if (isValid == false)
			{
				errors.rejectValue("appId", "", "Invalid AppId");
			}

			// Check if user can create another app
			org.prot.app.services.UserService platUserService = UserServiceFactory.getUserService();
			PlatformUser user = userService.getUser(platUserService.getCurrentUser());
			Set<String> apps = appService.getApplications(platUserService.getCurrentUser());
			if(user.getMaxApps() <= apps.size())
				errors.rejectValue("appId", "", "Cannot create another App. Maximal number of Apps is: " + user.getMaxApps());
				
			// Check if appId already exists
			boolean exists = appService.existsApplication(registeredApp.getAppId()); 
			if(exists == true)
			{
				errors.rejectValue("appId", "", "AppId already exists"); 
			}
		}
	}

	public void setAppService(AppService appService)
	{
		this.appService = appService;
	}

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}
}

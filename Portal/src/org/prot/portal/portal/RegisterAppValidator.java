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
package org.prot.portal.portal;

import java.util.Set;

import org.prot.app.services.user.UserServiceFactory;
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

		// Convert appId to lower case
		registeredApp.setAppId(registeredApp.getAppId().toLowerCase());

		// Check fields
		ValidationUtils.rejectIfEmpty(errors, "appId", "", "AppId required");

		// Do some further checks (if no errors until now)
		if (!errors.hasErrors())
		{
			// Check if appId is valid
			String isValid = ReservedAppIds.validateNewAppId(registeredApp.getAppId());
			if (isValid == null)
			{
				errors.rejectValue("appId", "", "Invalid AppId");
			}

			// Check if user can create another app
			org.prot.app.services.user.UserService platUserService = UserServiceFactory.getUserService();
			PlatformUser user = userService.getUser(platUserService.getCurrentUser());
			Set<String> apps = appService.getApplications(platUserService.getCurrentUser());
			if (user.getMaxApps() <= apps.size())
				errors.rejectValue("appId", "", "Cannot create another App. Maximal number of Apps is: "
						+ user.getMaxApps());

			// Check if appId already exists
			boolean exists = appService.existsApplication(registeredApp.getAppId());
			if (exists == true)
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

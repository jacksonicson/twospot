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
		userService.registerUser(user);
	}

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}
}

package org.prot.portal;

import org.springframework.web.servlet.mvc.SimpleFormController;

public class RegisterAppController extends SimpleFormController
{
	public RegisterAppController()
	{
		setCommandClass(RegisterAppCommand.class);
		setCommandName("registerAppCommand");
	}

	
}

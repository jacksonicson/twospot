package org.prot.portal.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class LoginHandlerController extends SimpleFormController
{
	private static final Logger logger = Logger.getLogger(LoginHandlerController.class);

	public LoginHandlerController()
	{
		setCommandClass(LoginCommand.class);
		setCommandName("loginCommand");
	}

	protected ModelAndView onSubmit(
			HttpServletRequest request,	HttpServletResponse response, Object command,	BindException errors)
			throws Exception {
	
		LoginCommand loginCommand = (LoginCommand)command;
		response.sendRedirect(loginCommand.getRedirectUrl());
		
		return null;
	}
}

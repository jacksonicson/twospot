package org.prot.portal.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

public class LoginHandlerController extends AbstractCommandController
{
	private static final Logger logger = Logger.getLogger(LoginHandlerController.class);

	public LoginHandlerController()
	{
		setCommandClass(LoginCommand.class);
		setCommandName("loginCommand");
	}

	@Override
	protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command,
			BindException exception) throws Exception
	{
		LoginCommand loginCommand = (LoginCommand)command; 
		response.sendRedirect(loginCommand.getRedirectUrl());
		
		logger.info("Ok redirecting user to the given url: " + loginCommand.getRedirectUrl());
		
		return null;
	}

}

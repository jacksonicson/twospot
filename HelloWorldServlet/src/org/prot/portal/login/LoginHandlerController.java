package org.prot.portal.login;

import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.prot.app.services.UserService;
import org.prot.app.services.UserServiceFactory;
import org.prot.util.Cookies;
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

	private String createGUID()
	{
		// TODO: Nor really unique!
		Random r = new Random();
		long a = r.nextLong(); 
		long b = r.nextLong(); 
		long c = System.currentTimeMillis(); 
		
		long res = Math.abs(a | b | c);
		
		return "" + res; 
	}
	
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command,
			BindException errors) throws Exception
	{
		// Get the login data
		LoginCommand loginCommand = (LoginCommand) command;
		
		// Calculate a GUID for the UID
		String uid = createGUID(); 
		
		// Login the user in the platform (privileged)
		UserService userService = UserServiceFactory.getUserService(); 
		userService.registerUser(uid, loginCommand.getUsername()); 
		
		// Set the UID cookie
		Cookie uidCookie = new Cookie(Cookies.USER_ID, uid);
		response.addCookie(uidCookie);
		
		// Redirect (if there is a destination url)
		if (loginCommand.getRedirectUrl() != null && loginCommand.getRedirectUrl().isEmpty() == false)
			response.sendRedirect(loginCommand.getRedirectUrl());

		return super.onSubmit(request, response, command, errors);
	}
}

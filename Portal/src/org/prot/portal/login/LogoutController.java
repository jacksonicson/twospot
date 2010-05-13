package org.prot.portal.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.prot.app.services.user.UserService;
import org.prot.app.services.user.UserServiceFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class LogoutController implements Controller
{

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		// Logout the user from the platform
		UserService userService = UserServiceFactory.getUserService();
		userService.unregisterUser();

		// Render the start page
		return new ModelAndView("start");
	}
}

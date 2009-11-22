package org.prot.portal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.prot.app.services.UserService;
import org.prot.app.services.UserServiceFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class StartController implements Controller
{
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		// Check if the user has a session
		UserService userService = UserServiceFactory.getUserService();
		String user = userService.getCurrentUser();

		if (user == null)
			return new ModelAndView("start");

		return new ModelAndView("portal");
	}
}

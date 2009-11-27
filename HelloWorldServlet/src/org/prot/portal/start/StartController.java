package org.prot.portal.start;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.prot.app.services.user.UserService;
import org.prot.app.services.user.UserServiceFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.InternalResourceView;

public class StartController implements Controller
{
	private static final Logger logger = Logger.getLogger(StartController.class);
	
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		// Check if the user has a session
		UserService userService = UserServiceFactory.getUserService();
		String user = userService.getCurrentUser();

		// If the user is not logged in
		if (user == null)
			return new ModelAndView("start");

		// if the user is loged go to the portal
		return new ModelAndView(new InternalResourceView("/portal.htm"));
	}
}

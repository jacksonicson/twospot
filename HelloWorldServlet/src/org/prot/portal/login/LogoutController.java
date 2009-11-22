package org.prot.portal.login;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.prot.app.services.UserService;
import org.prot.app.services.UserServiceFactory;
import org.prot.util.Cookies;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class LogoutController implements Controller
{

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		// Delete the UID-Cookie
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies)
		{
			if (cookie.getName().equals(Cookies.USER_ID))
			{
				cookie.setMaxAge(0);
				cookie.setValue("null");
				response.addCookie(cookie);
				break;
			}
		}

		// Logout the user from the platform
		UserService userService = UserServiceFactory.getUserService(); 
		userService.unregisterUser(); 

		// Render the start page 
		return new ModelAndView("start");
	}

}

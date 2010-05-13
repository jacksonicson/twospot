package org.prot.portal.login;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class LoginController implements Controller
{
	private static final Logger logger = Logger.getLogger(LoginController.class);

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		String redirectUrl = request.getParameter("url");
		String cancelUrl = request.getParameter("cancel");

		LoginCommand login = new LoginCommand();
		login.setRedirectUrl(redirectUrl);
		login.setCancelUrl(cancelUrl);

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("loginCommand", login);

		return new ModelAndView("loginHandler", model);
	}
}

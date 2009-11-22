package org.prot.portal;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.prot.app.services.UserService;
import org.prot.app.services.UserServiceFactory;
import org.prot.portal.services.AppService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class PortalController implements Controller
{
	private static final Logger logger = Logger.getLogger(PortalController.class);

	private AppService appService;
	
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		logger.info("Portal controller"); 
		
		UserService userService = UserServiceFactory.getUserService(); 
		String user = userService.getCurrentUser();
		
		logger.info("Portal controller: " + user);
		
		Set<String> appIds = appService.getApplications(user);
		
		return new ModelAndView("portal", "appIds", appIds);
	}

	public void setAppService(AppService appService)
	{
		this.appService = appService;
	}
}

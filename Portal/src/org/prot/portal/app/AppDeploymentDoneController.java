package org.prot.portal.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.prot.app.services.user.UserService;
import org.prot.app.services.user.UserServiceFactory;
import org.prot.portal.services.AppService;
import org.prot.portal.services.DeploymentService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class AppDeploymentDoneController implements Controller
{
	private static final Logger logger = Logger.getLogger(AppDeploymentDoneController.class);

	private AppService appService;

	private DeploymentService deploymentService;

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		logger.debug("Finishing deployment");
		
		// Get current user
		UserService userService = UserServiceFactory.getUserService();
		String user = userService.getCurrentUser();
		if (user == null)
		{
			response.sendError(400, "Unauthorized access");
			logger.debug("Unauthroized access");
			return null;
		}

		// Get deployment parameters
		String appId = request.getParameter("id").toLowerCase();
		String version = request.getParameter("ver").toLowerCase();
		if (appId == null || version == null)
		{
			response.sendError(400, "Deployment requires an appId and a version information");
			logger.debug("Missing AppId");
			return null;
		}

		// Check if the application exists
		String owner = appService.getApplicationOwner(appId);
		if (owner == null)
		{
			response.sendError(400, "AppId: " + appId
					+ " does not exist, cannot deploy a non existing application");
			logger.debug("AppId: " + appId + " does not exist, cannot deploy a non existing application");
			return null;
		}

		// Check if the user owns the application
		if (owner.equals(user) == false)
		{
			response.sendError(400, "User " + user + " does not own the AppId: " + appId);
			logger.debug("User " + user + " does not own the AppId: " + appId);
			return null;
		}

		// Ok - Ready for deployment
		logger.info("Committing " + appId + " now");

		// Finish the deployment
		deploymentService.deployApplication(appId, version);

		response.setStatus(HttpStatus.OK_200);
		return null;
	}

	public void setAppService(AppService appService)
	{
		this.appService = appService;
	}

	public void setDeploymentService(DeploymentService deploymentService)
	{
		this.deploymentService = deploymentService;
	}
}

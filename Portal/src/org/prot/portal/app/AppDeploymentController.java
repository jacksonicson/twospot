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

public class AppDeploymentController implements Controller
{
	private static final Logger logger = Logger.getLogger(AppDeploymentController.class);

	private AppService appService;

	private DeploymentService deploymentService;

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		logger.debug("Deploying application");

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
		String appId = request.getParameter("id");
		String version = request.getParameter("ver");
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
		logger.info("Deploying " + appId + " now");

		// Announce the deployment
		String token = deploymentService.announceDeployment(appId, version);
		if (token == null)
		{
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500);
			return null;
		}

		response.setStatus(HttpStatus.OK_200);
		response.getWriter().print(token);
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

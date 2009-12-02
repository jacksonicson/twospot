package org.prot.portal.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.prot.app.services.platform.PlatformService;
import org.prot.app.services.platform.PlatformServiceFactory;
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

		// Load the whole application package
		int status = 505;
		if (request.getContentLength() > 0)
		{
			// Stream everything to the fileserver (don't buffer the whole file)
			status = deploymentService.deployApplication(appId, version, request.getInputStream());

			// Inform the platform about the deployment
			logger.info("Imform platform about the deployment");
			PlatformService pservice = PlatformServiceFactory.getPlatformService();
			pservice.appDeployed(appId, version);
		}

		response.setStatus(status);
		response.getWriter().write("ok");

		// Don't render anything
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

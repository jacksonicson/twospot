package org.prot.portal.portal;

import org.prot.app.services.platform.PlatformService;
import org.prot.app.services.platform.PlatformServiceFactory;
import org.prot.portal.services.AppService;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class RegisterAppController extends SimpleFormController
{
	private AppService appService;

	public RegisterAppController()
	{
		setCommandClass(RegisterAppCommand.class);
		setCommandName("registerAppCommand");
	}

	protected void doSubmitAction(Object command) throws Exception
	{
		RegisterAppCommand regAppCommand = (RegisterAppCommand) command;
		PlatformService platformService = PlatformServiceFactory.getPlatformService();

		if (platformService.register(regAppCommand.getAppId(), null))
			appService.registerApplication(regAppCommand.getAppId());
	}

	public void setAppService(AppService appService)
	{
		this.appService = appService;
	}
}

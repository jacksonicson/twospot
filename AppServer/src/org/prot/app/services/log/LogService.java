package org.prot.app.services.log;

import java.util.List;

import org.apache.log4j.Logger;
import org.prot.appserver.config.Configuration;

public final class LogService
{
	private static final Logger logger = Logger.getLogger(LogService.class);

	private final org.prot.controller.services.log.LogService logService;

	LogService(org.prot.controller.services.log.LogService logService)
	{
		this.logService = logService;
	}

	private void log(String message, int severity)
	{
		String token = Configuration.getInstance().getAuthenticationToken();
		String appId = Configuration.getInstance().getAppId();
		logService.log(token, appId, message, severity);
	}

	public List<String> getMessages(String appId, int severity)
	{
		String token = Configuration.getInstance().getAuthenticationToken();
		return logService.getMessages(token, appId, severity);
	}

	public void debug(String message)
	{
		log(message, 0);
	}

	public void info(String message)
	{
		log(message, 1);
	}

	public void warn(String message)
	{
		log(message, 2);
	}

	public void error(String message)
	{
		log(message, 3);
	}
}

package org.prot.app.services.log;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.prot.appserver.config.Configuration;

public final class LogServiceImpl implements LogService
{
	private static final Logger logger = Logger.getLogger(LogServiceImpl.class);

	private final org.prot.controller.services.log.LogService logService;

	LogServiceImpl(org.prot.controller.services.log.LogService logService)
	{
		this.logService = logService;
	}

	private void log(String message, int severity)
	{
		String token = Configuration.getInstance().getAuthenticationToken();
		String appId = Configuration.getInstance().getAppId();
		logService.log(token, appId, message, severity);
	}

	public List<LogMessage> getMessages(String appId, int severity)
	{
		// TODO: This implementation is not very nice. The LogMessage class
		// should be moved to the Utils where its available to the appserver,
		// controller and apps
		String token = Configuration.getInstance().getAuthenticationToken();
		List<org.prot.controller.services.log.LogMessage> result = logService.getMessages(token, appId,
				severity);
		List<LogMessage> messages = new ArrayList<LogMessage>();
		for (org.prot.controller.services.log.LogMessage copy : result)
		{
			LogMessage newMessage = new LogMessage();
			newMessage.setMessage(copy.getMessage());
			newMessage.setSeverity(copy.getSeverity());
			messages.add(newMessage);
		}

		return messages;
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

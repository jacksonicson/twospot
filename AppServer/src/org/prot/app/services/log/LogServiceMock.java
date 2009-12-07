package org.prot.app.services.log;

import java.util.List;

import org.apache.log4j.Logger;

public class LogServiceMock implements LogService
{
	private static final Logger logger = Logger.getLogger(LogServiceMock.class);

	@Override
	public void debug(String message)
	{
		logger.debug(message);
	}

	@Override
	public void error(String message)
	{
		logger.error(message);
	}

	@Override
	public List<String> getMessages(String appId, int severity)
	{
		// This is a privileged method
		return null;
	}

	@Override
	public void info(String message)
	{
		logger.info(message);
	}

	@Override
	public void warn(String message)
	{
		logger.warn(message);
	}
}

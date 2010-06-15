/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
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
	public List<LogMessage> getMessages(String appId, int severity)
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

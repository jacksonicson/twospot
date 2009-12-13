package org.prot.appserver.management;

import org.apache.log4j.Logger;

class MockManagement implements AppManagement
{
	private static final Logger logger = Logger.getLogger(MockManagement.class);

	@Override
	public long connectionCount()
	{
		logger.warn("Using mock management");
		return 0;
	}

	@Override
	public long requestCount()
	{
		logger.warn("Using mock management");
		return 0;
	}

	@Override
	public double requestsPerSecond()
	{
		logger.warn("Using mock management");
		return 0;
	}

	@Override
	public void reset()
	{
		logger.warn("Using mock management");
	}
}

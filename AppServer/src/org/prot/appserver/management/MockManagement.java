package org.prot.appserver.management;

class MockManagement implements AppManagement
{
	@Override
	public long connectionCount()
	{
		return 0;
	}

	@Override
	public long requestCount()
	{
		return 0;
	}

	@Override
	public double requestsPerSecond()
	{
		return 0;
	}

	@Override
	public void reset()
	{
	}
}

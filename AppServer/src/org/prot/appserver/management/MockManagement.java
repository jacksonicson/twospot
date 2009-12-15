package org.prot.appserver.management;

class MockManagement implements AppManagement
{
	@Override
	public double requestsPerSecond()
	{
		return 0;
	}

	@Override
	public long averageRequestTime()
	{
		return 0;
	}

	@Override
	public double ping()
	{
		return 0d;
	}
}

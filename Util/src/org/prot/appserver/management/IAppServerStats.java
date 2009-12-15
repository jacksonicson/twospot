package org.prot.appserver.management;

public interface IAppServerStats
{
	public double getRequestsPerSecond();

	public long averageRequestTime();

	public double ping();
}

package org.prot.appserver.management;

public interface AppManagement
{
	public double requestsPerSecond();

	public long connectionCount();

	public long requestCount();

	public void reset();
}

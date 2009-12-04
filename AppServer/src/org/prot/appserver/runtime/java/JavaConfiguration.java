package org.prot.appserver.runtime.java;

import org.prot.appserver.app.RuntimeConfiguration;

public class JavaConfiguration implements RuntimeConfiguration
{
	private boolean useDistributedSessions = false;

	public boolean isUseDistributedSessions()
	{
		return useDistributedSessions;
	}

	public void setUseDistributedSessions(boolean useDistributedSessions)
	{
		this.useDistributedSessions = useDistributedSessions;
	} 
}

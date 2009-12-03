package org.prot.app.services.log;

import java.util.List;

public class MockLogDao implements LogDao
{
	@Override
	public List<String> getLog(String appId)
	{
		// Not implemented in this mock implementation
		return null; 
	}

	@Override
	public void writeLog(String appId, int priority, String message, String stack)
	{
		System.out.println(appId + " - " + priority + ": " + message);
	}
}

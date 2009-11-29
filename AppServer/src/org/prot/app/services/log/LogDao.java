package org.prot.app.services.log;

import java.util.List;

public interface LogDao
{
	public void writeLog(String appId, int priority, String message, String stack);
	
	public List<String> getLog(String appId);
}

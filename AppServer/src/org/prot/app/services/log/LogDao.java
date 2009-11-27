package org.prot.app.services.log;

public interface LogDao
{
	public void writeLog(String appId, int priority, String message, String stack);
}

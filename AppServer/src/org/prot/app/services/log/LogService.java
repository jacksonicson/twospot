package org.prot.app.services.log;

public class LogService
{
	private final String appId;
	
	private final LogDao logDao;
	
	LogService(String appId, LogDao logDao)
	{
		this.appId = appId;
		this.logDao = logDao;
	}
	
	public void debug(String message)
	{
		logDao.writeLog(appId, 0, message, "todo"); 
	}

	public void info(String message)
	{
		logDao.writeLog(appId, 1, message, "todo");
	}

	public void error(String message)
	{
		logDao.writeLog(appId, 2, message, "todo");
	}
}

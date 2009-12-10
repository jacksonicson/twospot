package org.prot.app.services.log;

import java.util.List;

public interface LogService
{
	public static final int ALL = -1;
	public static final int DEBUG = 0;
	public static final int INFO = 1;
	public static final int WARN = 2;
	public static final int ERROR = 3;

	public List<String> getMessages(String appId, int severity);

	public void debug(String message);

	public void info(String message);

	public void warn(String message);

	public void error(String message);
}

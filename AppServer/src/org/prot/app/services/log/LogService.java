package org.prot.app.services.log;

import java.util.List;

public interface LogService
{
	public List<String> getMessages(String appId, int severity);

	public void debug(String message);

	public void info(String message);

	public void warn(String message);

	public void error(String message);
}

package org.prot.controller.services.log;

import java.util.List;

import org.prot.controller.services.PrivilegedAppServer;

public interface LogService
{
	@PrivilegedAppServer
	public void log(String token, String appId, String message, int severity);

	@PrivilegedAppServer
	public List<LogMessage> getMessages(String token, String appId, int severity);
}

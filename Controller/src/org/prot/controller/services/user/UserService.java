package org.prot.controller.services.user;

import org.prot.controller.services.PrivilegedAppServer;

public interface UserService
{
	public boolean getCurrentUser(String uid);

	public String getLoginUrl(String redirectUrl);

	@PrivilegedAppServer
	public void registerUser(String token, String uid);
}

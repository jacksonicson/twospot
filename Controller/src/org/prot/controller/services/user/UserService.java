package org.prot.controller.services.user;

import org.prot.controller.services.PrivilegedAppServer;

public interface UserService
{
	public String getCurrentUser(String uid);

	public String getLoginUrl(String redirectUrl, String cancelUrl);

	public void unregisterUser(String uid);

	@PrivilegedAppServer
	public void registerUser(String token, String uid, String username);
}

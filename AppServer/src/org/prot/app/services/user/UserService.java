package org.prot.app.services.user;

public interface UserService
{
	public String getCurrentUser();

	public String getLoginUrl(String redirectUrl, String cancelUrl);

	public void registerUser(String uid, String username);

	public void unregisterUser();
}

package org.prot.app.services.user;

public interface UserService
{
	public String getCurrentUser();

	public String getLoginUrl(String redirectUrl, String cancelUrl);

	public void registerUser(final String uid, final String username);

	public void unregisterUser();
}

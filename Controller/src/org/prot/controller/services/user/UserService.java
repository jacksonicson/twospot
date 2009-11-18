package org.prot.controller.services.user;

public interface UserService
{
	public boolean getCurrentUser(String session);

	public String getLoginUrl();
}

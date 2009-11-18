package org.prot.controller.services;

public interface UserService
{
	public boolean getCurrentUser(String session);

	public String getLoginUrl();
}

package org.prot.controller.services;

public interface PrivilegedUserService extends UserService
{
	public void registerSession(String token, String session);
}

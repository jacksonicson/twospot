package org.prot.controller.services.user;

public interface PrivilegedUserService extends UserService
{
	public void registerSession(String token, String session);
}

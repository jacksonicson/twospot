package org.prot.portal.login.data;

public interface UserDao
{
	public void saveUser(PlatformUser user);
	
	public void getUser(String username); 
}

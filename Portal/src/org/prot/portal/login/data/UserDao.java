package org.prot.portal.login.data;

public interface UserDao
{
	public void saveUser(PlatformUser user);
	
	public void updateUser(PlatformUser user); 
	
	public PlatformUser getUser(String username); 
}

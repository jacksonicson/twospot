package org.prot.portal.login;

import org.prot.portal.login.data.PlatformUser;

public class RegisterCommand extends PlatformUser
{
	// Plaintext passwords from the registration form
	private String password0; 
	
	private String password1;

	public String getPassword0()
	{
		return password0;
	}

	public void setPassword0(String password0)
	{
		this.password0 = password0;
	}

	public String getPassword1()
	{
		return password1;
	}

	public void setPassword1(String password1)
	{
		this.password1 = password1;
	}
}

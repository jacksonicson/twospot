package org.prot.portal.login;

public class LoginCommand
{
	private String redirectUrl;
	
	private String cancelUrl;

	private String username;

	private String password;

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getRedirectUrl()
	{
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl)
	{
		this.redirectUrl = redirectUrl;
	}

	public String getCancelUrl()
	{
		return cancelUrl;
	}

	public void setCancelUrl(String cancelUrl)
	{
		this.cancelUrl = cancelUrl;
	}
}

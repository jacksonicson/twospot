package org.prot.portal.login.data;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class PlatformUser
{
	@PrimaryKey
	@Persistent
	private String username;

	@Persistent
	private String md5Password;

	@Persistent
	private int maxApps;

	@Persistent
	private String email;

	@Persistent
	private String surname;

	@Persistent
	private String forename;

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getMd5Password()
	{
		return md5Password;
	}

	public void setMd5Password(String md5Password)
	{
		this.md5Password = md5Password;
	}

	public int getMaxApps()
	{
		return maxApps;
	}

	public void setMaxApps(int maxApps)
	{
		this.maxApps = maxApps;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getSurname()
	{
		return surname;
	}

	public void setSurname(String surname)
	{
		this.surname = surname;
	}

	public String getForename()
	{
		return forename;
	}

	public void setForename(String forename)
	{
		this.forename = forename;
	}
	
	public PlatformUser clone()
	{
		PlatformUser platformUser = new PlatformUser(); 
		platformUser.setUsername(username);
		platformUser.setSurname(surname);
		platformUser.setForename(forename);
		platformUser.setEmail(email);
		platformUser.setMaxApps(maxApps);
		platformUser.setMd5Password(md5Password);
		return platformUser;
	}
}

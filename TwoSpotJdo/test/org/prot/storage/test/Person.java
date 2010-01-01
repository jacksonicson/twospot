package org.prot.storage.test;

import java.io.Serializable;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.prot.storage.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Person implements Serializable
{
	private static final long serialVersionUID = 6093869629637484709L;

	@Persistent(customValueStrategy = "keygen")
	@PrimaryKey
	private Key key;

	@Persistent
	private String username;

	@Persistent
	private String message;

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public Key getKey()
	{
		return key;
	}

	public void setKey(Key key)
	{
		this.key = key;
	}
}

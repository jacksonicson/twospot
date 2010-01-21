package org.prot.controller.services.user;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;

import org.prot.storage.Key;

@PersistenceCapable
public class UserSession implements Serializable
{
	private static final long serialVersionUID = 7086114393996041323L;

	@PrimaryKey
	@Persistent(customValueStrategy = "keygen")
	private Key key;

	@Persistent
	@Unique
	private String username;

	@Persistent
	private String sessionId;

	@Persistent
	private long timestamp;

	public Key getKey()
	{
		return key;
	}

	public void setKey(Key key)
	{
		this.key = key;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}
}

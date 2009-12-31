package org.prot.appserver.runtime.java.data;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.prot.storage.Key;

@PersistenceCapable
public class SessionId
{
	@PrimaryKey
	@Persistent(customValueStrategy = "keygen")
	private Key key;

	@Persistent(valueStrategy = IdGeneratorStrategy.UUIDSTRING)
	private String sessionId;

	public SessionId()
	{
		// Do nothing
	}

	public Key getKey()
	{
		return key;
	}

	public void setKey(Key key)
	{
		this.key = key;
	}

	public SessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}
}

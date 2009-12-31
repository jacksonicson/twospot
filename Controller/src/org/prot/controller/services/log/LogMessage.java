package org.prot.controller.services.log;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.prot.storage.Key;

@PersistenceCapable
public final class LogMessage implements Serializable
{
	private static final long serialVersionUID = 5182491041865229373L;

	@PrimaryKey
	@Persistent(customValueStrategy = "keygen")
	private Key key;

	@Persistent
	private String id;

	@Persistent
	private String appId;

	@Persistent
	private String message;

	@Persistent
	private int severity;

	public LogMessage()
	{

	}

	public Key getKey()
	{
		return key;
	}

	public void setKey(Key key)
	{
		this.key = key;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getAppId()
	{
		return appId;
	}

	public void setAppId(String appId)
	{
		this.appId = appId;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public int getSeverity()
	{
		return severity;
	}

	public void setSeverity(int severity)
	{
		this.severity = severity;
	}
}

package org.prot.portal.app.data;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.prot.storage.Key;

@PersistenceCapable
public class Application implements Serializable
{
	private static final long serialVersionUID = 5204627461851624628L;

	@PrimaryKey
	@Persistent(customValueStrategy = "keygen")
	private Key key;

	@Persistent
	private String appId;

	@Persistent
	private String owner;

	public Key getKey()
	{
		return key;
	}

	public void setKey(Key key)
	{
		this.key = key;
	}

	public String getAppId()
	{
		return appId;
	}

	public void setAppId(String appId)
	{
		this.appId = appId.toLowerCase();
	}

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		this.owner = owner;
	}
}

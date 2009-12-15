package org.prot.controller.services.log;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.hadoop.hbase.util.Bytes;

@PersistenceCapable
public final class LogMessage implements Serializable
{
	private static final long serialVersionUID = 5182491041865229373L;

	@PrimaryKey
	@Persistent
	private byte[] key;

	@Persistent
	private String id;

	@Persistent
	private String appId;

	@Persistent
	private String message;

	@Persistent
	private int severity;

	public static final byte[] buildKey(String appId, String random)
	{
		byte[] key = new byte[28];

		// Create the byte sequence for the AppId (20 bytes)
		byte[] buffer = Bytes.toBytes(appId);
		key = Bytes.add(key, buffer);
		for (int i = buffer.length; i < 20; i++)
			key[i] = 0;

		// Insert the timestamp (8 bytes)
		byte[] time = Bytes.toBytes(System.currentTimeMillis());
		key = Bytes.add(key, time);

		return key;
	}

	public byte[] getKey()
	{
		return key;
	}

	public void setKey(byte[] key)
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

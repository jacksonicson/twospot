package org.prot.appserver.runtime.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class SessionData implements Serializable
{
	private static final long serialVersionUID = -7629526712017876974L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.UUIDSTRING)
	private String dbid;

	@Persistent
	private String sessionId;

	// Timesampt of the last access to this session
	@Persistent
	private long accessed;

	@Persistent
	private long lastAccessed;

	@Persistent
	private long maxIdleMs;

	@Persistent
	private long cookieSet;

	// Timestamp when this session was created
	@Persistent
	private long created;

	// TODO: Identifies the last node where this session was used (not
	// applicable here)
	@Persistent
	private String lastNode;

	@Persistent
	private String canonicalContext;

	@Persistent
	private long lastSaved;

	@Persistent
	private long expiryTime;

	@Persistent
	private String virtualHost;

	// Stores the session attributes
	@NotPersistent
	volatile private Map attributes;

	// Byte arry wich contains the serialized attributes
	@Persistent
	private byte[] serializedAttributes;

	public SessionData(String sessionId)
	{
		this.sessionId = sessionId;
		this.created = System.currentTimeMillis();
		this.accessed = created;
		this.attributes = new ConcurrentHashMap<Object, Object>();
	}

	public void restoreSerialization() throws IOException, ClassNotFoundException
	{
		ByteArrayInputStream bytes = new ByteArrayInputStream(serializedAttributes);
		ObjectInputStream in = new ObjectInputStream(bytes);
		Object object = in.readObject();
		attributes = (Map) object;
		in.close();
	}

	public void prepareSerialization() throws IOException
	{
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bytes);
		out.writeObject(attributes);
		serializedAttributes = bytes.toByteArray();
		out.close();
	}

	public synchronized long getAccessed()
	{
		return accessed;
	}

	public synchronized void setAccessed(long accessed)
	{
		this.accessed = accessed;
	}

	public synchronized long getLastAccessed()
	{
		return lastAccessed;
	}

	public synchronized void setLastAccessed(long lastAccessed)
	{
		this.lastAccessed = lastAccessed;
	}

	public synchronized long getMaxIdleMs()
	{
		return maxIdleMs;
	}

	public synchronized void setMaxIdleMs(long maxIdleMs)
	{
		this.maxIdleMs = maxIdleMs;
	}

	public synchronized long getCookieSet()
	{
		return cookieSet;
	}

	public synchronized void setCookieSet(long cookieSet)
	{
		this.cookieSet = cookieSet;
	}

	public synchronized long getCreated()
	{
		return created;
	}

	public synchronized void setCreated(long created)
	{
		this.created = created;
	}

	public synchronized String getLastNode()
	{
		return lastNode;
	}

	public synchronized void setLastNode(String lastNode)
	{
		this.lastNode = lastNode;
	}

	public synchronized String getCanonicalContext()
	{
		return canonicalContext;
	}

	public synchronized void setCanonicalContext(String canonicalContext)
	{
		this.canonicalContext = canonicalContext;
	}

	public synchronized long getLastSaved()
	{
		return lastSaved;
	}

	public synchronized void setLastSaved(long lastSaved)
	{
		this.lastSaved = lastSaved;
	}

	public synchronized long getExpiryTime()
	{
		return expiryTime;
	}

	public synchronized void setExpiryTime(long expiryTime)
	{
		this.expiryTime = expiryTime;
	}

	public synchronized String getVirtualHost()
	{
		return virtualHost;
	}

	public synchronized void setVirtualHost(String virtualHost)
	{
		this.virtualHost = virtualHost;
	}

	public synchronized Map getAttributes()
	{
		return attributes;
	}

	public synchronized void setAttributes(Map attributes)
	{
		this.attributes = attributes;
	}

	public synchronized String getSessionId()
	{
		return sessionId;
	}

	public synchronized void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public synchronized String getDbid()
	{
		return dbid;
	}

	public synchronized void setDbid(String dbid)
	{
		this.dbid = dbid;
	}
}
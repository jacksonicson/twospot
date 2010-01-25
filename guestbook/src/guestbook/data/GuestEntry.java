package guestbook.data;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.prot.storage.Key;

@PersistenceCapable
public class GuestEntry
{
	@Persistent(customValueStrategy = "keygen")
	@PrimaryKey
	private Key key;

	@Persistent
	private String name;

	@Persistent
	private String message;
	
	@Persistent
	private long timestamp; 

	public GuestEntry()
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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public long isTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}
}

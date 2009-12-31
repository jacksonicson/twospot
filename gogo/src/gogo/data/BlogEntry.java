package gogo.data;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.prot.storage.Key;

@PersistenceCapable
public class BlogEntry
{
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

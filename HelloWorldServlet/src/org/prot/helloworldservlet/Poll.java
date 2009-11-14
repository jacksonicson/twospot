package org.prot.helloworldservlet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


@PersistenceCapable
public class Poll
{
	@PrimaryKey
	@Persistent
	private String title = null;

	@Persistent
	private String poll = null;

	@Persistent
	private Long time = null;
	
	@Persistent
	private String test = null; 

	public Poll()
	{
		// Required
	}

	public Poll(String poll, Long time)
	{
		this.poll = poll;
		this.time = time;

		this.title = time + poll;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getPoll()
	{
		return poll;
	}

	public void setPoll(String poll)
	{
		this.poll = poll;
	}

	public Long getTime()
	{
		return time;
	}

	public void setTime(Long time)
	{
		this.time = time;
	}

	public String getTest()
	{
		return test;
	}

	public void setTest(String test)
	{
		this.test = test;
	}
}

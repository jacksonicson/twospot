/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
package org.prot.storage.test.data;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.prot.storage.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Person
{
	@Persistent(customValueStrategy = "keygen")
	@PrimaryKey
	private Key key;

	@Persistent
	private String username;

	@Persistent
	private String message;

	@Persistent
	private long time;

	@Persistent
	private long asdf;

	@Persistent
	private double type;

	public double getType()
	{
		return type;
	}

	public void setType(double type)
	{
		this.type = type;
	}

	public long getAsdf()
	{
		return asdf;
	}

	public void setAsdf(long asdf)
	{
		this.asdf = asdf;
	}

	public long getTime()
	{
		return time;
	}

	public void setTime(long time)
	{
		this.time = time;
	}

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

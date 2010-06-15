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
package org.prot.portal.login.data;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.prot.storage.Key;

@PersistenceCapable
public class PlatformUser implements Serializable
{
	private static final long serialVersionUID = 1858714976360637838L;

	@PrimaryKey
	@Persistent(customValueStrategy = "keygen")
	private Key key;

	@Persistent
	private String username;

	@Persistent
	private String md5Password;

	@Persistent
	private int maxApps = 3;

	@Persistent
	private String email;

	@Persistent
	private String surname;

	@Persistent
	private String forename;

	public PlatformUser()
	{

	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username.toLowerCase();
	}

	public String getMd5Password()
	{
		return md5Password;
	}

	public void setMd5Password(String md5Password)
	{
		this.md5Password = md5Password;
	}

	public int getMaxApps()
	{
		return maxApps;
	}

	public void setMaxApps(int maxApps)
	{
		this.maxApps = maxApps;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getSurname()
	{
		return surname;
	}

	public void setSurname(String surname)
	{
		this.surname = surname;
	}

	public String getForename()
	{
		return forename;
	}

	public void setForename(String forename)
	{
		this.forename = forename;
	}

	public Key getKey()
	{
		return key;
	}

	public void setKey(Key key)
	{
		this.key = key;
	}

	public PlatformUser clone()
	{
		PlatformUser platformUser = new PlatformUser();
		platformUser.setUsername(username);
		platformUser.setSurname(surname);
		platformUser.setForename(forename);
		platformUser.setEmail(email);
		platformUser.setMaxApps(maxApps);
		platformUser.setMd5Password(md5Password);
		return platformUser;
	}
}

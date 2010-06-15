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
package org.prot.portal.login;

import org.prot.portal.login.data.PlatformUser;

public class RegisterCommand extends PlatformUser
{
	// Plaintext passwords from the registration form
	private String password0; 
	
	private String password1;

	public String getPassword0()
	{
		return password0;
	}

	public void setPassword0(String password0)
	{
		this.password0 = password0;
	}

	public String getPassword1()
	{
		return password1;
	}

	public void setPassword1(String password1)
	{
		this.password1 = password1;
	}
}

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
package org.prot.appserver.runtime.jython;

public class WebConfiguration
{
	private String regExp;
	private String pythonFile;

	public WebConfiguration(String regExp, String pythonFile)
	{
		this.regExp = regExp;
		this.pythonFile = pythonFile;
	}

	public String matches(String uri)
	{
		if (uri.matches(regExp))
		{
			return this.pythonFile;
		}

		// URI does not match
		return null;
	}
}

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
package gogo;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

public class DataConnection
{
	private static PersistenceManagerFactory pmf;

	public static PersistenceManager getManager()
	{
		if (pmf == null)
		{
			pmf = JDOHelper.getPersistenceManagerFactory("etc/jdoDefault.properties");
		}

		return pmf.getPersistenceManager();
	}
}

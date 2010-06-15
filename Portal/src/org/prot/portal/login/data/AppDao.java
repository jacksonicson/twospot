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

import java.util.Set;

import org.prot.portal.app.data.Application;

public interface AppDao
{
	public Application loadApp(String appId); 
	
	public Set<String> getApps(String owner);
	
	public void saveApp(String appId, String owner);
}

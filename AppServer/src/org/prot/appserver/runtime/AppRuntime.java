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
package org.prot.appserver.runtime;

import java.util.Map;

import org.prot.appserver.app.AppInfo;
import org.prot.appserver.management.RuntimeManagement;

public interface AppRuntime
{
	public String getIdentifier();

	public void loadConfiguration(AppInfo appInfo, Map<?, ?> yaml);

	public void launch(AppInfo appInfo) throws Exception;

	public RuntimeManagement getManagement();
}

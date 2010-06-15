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
package org.prot.frontend.cache;

import org.prot.manager.stats.ControllerInfo;

public final class CacheResult
{
	private final ControllerInfo controllerInfo;

	private final String appId;  

	public CacheResult(ControllerInfo controllerInfo, String appId)
	{
		this.controllerInfo = controllerInfo;
		this.appId = appId;
	}

	public ControllerInfo getControllerInfo()
	{
		return controllerInfo;
	}
	
	public String getAppId()
	{
		return appId;
	}
}

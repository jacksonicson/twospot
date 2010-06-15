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
package org.prot.manager.services;

import java.util.Set;

import org.prot.manager.stats.ControllerInfo;

public interface FrontendService
{
	/**
	 * @param appId
	 * @return null if there was an error or there are no controllers available
	 */
	public Set<ControllerInfo> selectController(String appId);
}

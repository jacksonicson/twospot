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
package org.prot.controller.app.lifecycle.appfetch;


public interface AppFetcher {
	/**
	 * Load the WAR-Archife for the application with the given AppId. Create a
	 * new AppInfo-Object which contains the bytes of the WAR-File and the
	 * AppId.
	 * 
	 * @param appId
	 * @return
	 */
	public byte[] fetchApp(String appId);
}

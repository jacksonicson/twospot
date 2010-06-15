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
package org.prot.jdo.storage;

public class StorageHelper {
	private static boolean devMode = false;

	private static String APP_ID = "null";

	private static ThreadLocal<String> TL_APP_ID = new ThreadLocal<String>();

	public static void setDevMode(boolean devMode) {
		StorageHelper.devMode = devMode;
	}

	public static boolean isDevMode() {
		return devMode;
	}

	public static void setTlAppId(String appId) {
		TL_APP_ID.set(appId);
	}

	public static void setAppId(String appId) {
		APP_ID = appId;
	}

	public static String getAppId() {
		return APP_ID;
	}
}
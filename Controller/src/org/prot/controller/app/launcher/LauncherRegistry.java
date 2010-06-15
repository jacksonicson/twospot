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
package org.prot.controller.app.launcher;

import java.util.List;

public class LauncherRegistry {

	private List<AppLauncher> launchers;

	public AppLauncher getLauncher(String identifier) {
		for (AppLauncher launcher : launchers) {
			if (launcher.getIdentifier().equalsIgnoreCase(identifier))
				return launcher;
		}

		return null;
	}

	public void setLaunchers(List<AppLauncher> launchers) {
		this.launchers = launchers;
	}
}

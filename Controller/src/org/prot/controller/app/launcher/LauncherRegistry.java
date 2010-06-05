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

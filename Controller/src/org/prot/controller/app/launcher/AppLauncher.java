package org.prot.controller.app.launcher;

import java.util.List;

import org.prot.controller.app.AppInfo;

public interface AppLauncher {

	public String getIdentifier();

	public List<String> createCommand(AppInfo appInfo, String baseDir);
}

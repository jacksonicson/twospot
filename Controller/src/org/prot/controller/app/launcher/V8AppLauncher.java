package org.prot.controller.app.launcher;

import java.util.LinkedList;
import java.util.List;

import org.prot.controller.app.AppInfo;
import org.prot.controller.config.Configuration;

public class V8AppLauncher implements AppLauncher {

	@Override
	public List<String> createCommand(AppInfo appInfo, String baseDir) {
		List<String> command = new LinkedList<String>();
		command.add(Configuration.getConfiguration().getTwoSpotV8Bin());

		command.add("--appId");
		command.add(appInfo.getAppId());

		command.add("--appSrvPort");
		command.add(appInfo.getPort() + "");

		command.add("--appDir");
		command.add(baseDir);

		command.add("--token");
		command.add("null");

		return command;
	}

	@Override
	public String getIdentifier() {
		return "JS";
	}
}

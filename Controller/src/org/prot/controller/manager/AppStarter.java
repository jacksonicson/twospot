package org.prot.controller.manager;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class AppStarter {

	public void stopApp(AppInfo info) {
		try {
			// shutdown the process itself
			info.getProcess().destroy();

			// stop the process manager
			info.stopProcessManager();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startApp(AppInfo info) {
		// check if there is an old process which is stale
		if (info.isStale()) {
			stopApp(info);
		}

		// build command linke
		List<String> command = new LinkedList<String>();
		command.add("java");
		command.add("-classpath");
		command.add(loadClasspath());
		command.add("org.prot.appserver.Main");

		command.add("-appId");
		command.add(info.getAppId());

		command.add("-ctrlPort");
		command.add("8079");
		
		command.add("-appSrvPort");
		command.add(info.getPort() + "");

		// configure the process
		ProcessBuilder procBuilder = new ProcessBuilder();
		procBuilder.directory(new File("../AppServer/"));
		procBuilder.command(command);
		procBuilder.redirectErrorStream(false);
		
		try {
			// start the process
			Process process = procBuilder.start();
			
			// process is not stale
			info.setStale(false);
			
			// monitor process
			info.startProcessManager(process);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String loadClasspath() {
		File libs = new File("../Libs/");
		String classpath = crawlDir(libs);

		File appServer = new File("../AppServer/bin");
		classpath += appServer.getAbsolutePath();

		return classpath;
	}

	private String crawlDir(File dir) {
		String jars = "";

		for (File subdir : dir.listFiles()) {

			if (subdir.isDirectory()) {
				String subjar = crawlDir(subdir);
				jars += subjar;
			} else {
				String filename = subdir.getName();
				if (filename.lastIndexOf(".") > 0)
					filename = filename.substring(filename.lastIndexOf("."));
				if (filename.equals(".jar"))
					jars += subdir.getAbsolutePath() + ";";
			}
		}

		return jars;
	}
}

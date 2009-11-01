package org.prot.controller.manager;


public class AppInfo {

	// app infos
	private String appId;

	private int port;

	// runtime
	private Process process;
	private ProcessManager manager = new ProcessManager(); 
	
	private boolean stale = false;

	public void stopProcessManager() {
		manager.stop(); 
	}
	
	public void startProcessManager(Process process) {
		this.process = process; 
		manager.start(process); 
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Process getProcess() {
		return process;
	}

	public boolean isStale() {
		return stale;
	}

	public void setStale(boolean stale) {
		this.stale = stale;
	}
}

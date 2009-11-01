package org.prot.controller.manager;

import java.io.ByteArrayOutputStream;

public class AppInfo {

	// app infos
	private String appId;

	private int port;

	// runtime
	private Process process;
	private ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	private ByteArrayOutputStream errStream = new ByteArrayOutputStream();

	public AppInfo() {
		// empty
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

	public void setProcess(Process process) {
		this.process = process;
	}

	public ByteArrayOutputStream getOutStream() {
		return outStream;
	}

	public ByteArrayOutputStream getErrStream() {
		return errStream;
	}

}

package org.prot.controller.services;

public interface ControllerService
{
	// Test if the controller process is still alive
	public void ping();
	
	// Gets the current system load
	public int getSystemLoad();
	
	// There is a new application version 
	public void updateApp(String appId);
	
	// The controller hast to kill the application
	public void killApp(String appId); 
}

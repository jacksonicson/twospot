package org.prot.controller.app;

public enum AppState
{
	// AppServer is offline
	OFFLINE,

	// AppServer is starting and cannot handle requests
	STARTING,

	// AppServer is online and can handle requests
	ONLINE,

	// Communication with the AppServer failed - it will be killed
	STALE,

	// An error occured while starting the AppServer - it will be killed
	FAILED,

	// The AppServer is idle - it will be killed
	IDLE,

	// AppServer explicitly killed - it will be killed and alle requests
	// for the AppServer will be blocked for a short time
	KILLED,
}

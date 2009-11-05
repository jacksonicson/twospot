package org.prot.manager.pojos;

import java.io.Serializable;

public class AppServerInfo implements Serializable
{
	private static final long serialVersionUID = -2515688211723045219L;

	private String controllerAddress;
	
	private int controllerPort;

	
	public String getControllerAddress()
	{
		return controllerAddress;
	}

	public void setControllerAddress(String controllerAddress)
	{
		this.controllerAddress = controllerAddress;
	}

	public int getControllerPort()
	{
		return controllerPort;
	}

	public void setControllerPort(int controllerPort)
	{
		this.controllerPort = controllerPort;
	} 
}

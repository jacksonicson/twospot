package org.prot.manager.config;

import java.io.Serializable;

public class ControllerInfo implements Serializable
{
	private String address;

	private int port;

	public ControllerInfo()
	{
		// empty constructor is necessary
	}

	public ControllerInfo(ControllerInfo info)
	{
		this.address = info.getAddress();
		this.port = info.getPort();
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}
}

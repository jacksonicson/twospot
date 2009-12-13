package org.prot.manager.data;

import java.io.Serializable;

public class ControllerInfo implements Serializable
{
	private static final long serialVersionUID = -4036981762748417669L;

	private String address;

	private int port;

	private String serviceAddress;

	private int servicePort;

	private String serviceName;

	private transient ManagementData managementData = new ManagementData();

	public ControllerInfo()
	{
		// empty constructor is required
	}

	public ControllerInfo(ControllerInfo info)
	{
		update(info);
	}

	public void update(ControllerInfo info)
	{
		this.address = info.getAddress();
		this.serviceAddress = info.getServiceAddress();
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

	public int getServicePort()
	{
		return servicePort;
	}

	public void setServicePort(int servicePort)
	{
		this.servicePort = servicePort;
	}

	public String getServiceName()
	{
		return serviceName;
	}

	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}

	public String getServiceAddress()
	{
		return serviceAddress;
	}

	public void setServiceAddress(String serviceAddress)
	{
		this.serviceAddress = serviceAddress;
	}

	public ManagementData getManagementData()
	{
		return managementData;
	}
}

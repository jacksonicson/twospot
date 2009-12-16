package org.prot.manager.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ControllerInfo implements Serializable
{
	private static final long serialVersionUID = -4036981762748417669L;

	private String address;

	private int port;

	private String serviceAddress;

	private int servicePort;

	private String serviceName;

	private transient ManagementData managementData;

	private transient Map<String, AssignedApp> assigned = new HashMap<String, AssignedApp>();

	public ControllerInfo()
	{
		managementData = new ManagementData();
	}

	public ControllerInfo(ControllerInfo info)
	{
		this();
		update(info);
	}

	public void assign(String appId)
	{
		assigned.put(appId, new AssignedApp(appId));
	}

	public int assignedSize()
	{
		// Remove all old assignments
		synchronized (assigned)
		{
			for (Iterator<String> it = assigned.keySet().iterator(); it.hasNext();)
			{
				String appId = it.next();
				AssignedApp app = assigned.get(appId);
				if (app.isOld())
					it.remove();
			}
		}

		// Determine size
		return assigned.size();
	}

	public boolean isAssigned(String appId)
	{
		AssignedApp assignedApp = assigned.get(appId);
		if (assignedApp == null)
			return false;

		synchronized (assigned)
		{
			if (assignedApp.isOld())
			{
				assigned.remove(appId);
				return false;
			}
		}

		return true;
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

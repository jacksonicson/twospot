package org.prot.util.zookeeper.data;

import java.io.Serializable;

public class AppEntry implements Serializable
{
	private static final long serialVersionUID = -1278966161100154367L;

	public final String appId;

	public AppEntry(String appId)
	{
		this.appId = appId;
	}
}

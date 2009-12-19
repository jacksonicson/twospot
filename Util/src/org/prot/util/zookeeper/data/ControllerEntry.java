package org.prot.util.zookeeper.data;

import java.io.Serializable;

public class ControllerEntry implements Serializable
{
	private static final long serialVersionUID = 5323196795384674010L;

	public String serviceAddress;

	public String address;

	public int port;
}

package org.prot.controller.app;

import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

public class PortPool
{
	private static final Logger logger = Logger.getLogger(PortPool.class);

	private final int startPort = 9090;

	private int currentPort = startPort;

	private Queue<Integer> freePorts = new LinkedList<Integer>();

	public void releasePort(int port)
	{
		freePorts.add(port);
	}

	public synchronized int getPort()
	{
		// Check if there are any free ports
		if (freePorts.isEmpty())
			return this.currentPort++;

		// Find a free port
		synchronized (freePorts)
		{
			Integer foundPort = null;
			for (Integer test : freePorts)
			{
				try
				{
					ServerSocket socket = new ServerSocket(test);
					socket.close();
					foundPort = test;
					break;
				} catch (Exception e)
				{
					logger.warn("AppRegsitry could not reuse port: " + test);
					continue;
				}
			}

			if (foundPort != null)
			{
				freePorts.remove(foundPort);
				return foundPort;
			}
		}

		return this.currentPort++;
	}
}

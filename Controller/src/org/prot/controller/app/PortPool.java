package org.prot.controller.app;

import java.net.ServerSocket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

public class PortPool {
	private static final Logger logger = Logger.getLogger(PortPool.class);

	private final int startPort = 9090;

	private int currentPort = startPort;

	private Queue<Integer> freePorts = new LinkedList<Integer>();

	private Queue<Integer> usedPorts = new LinkedList<Integer>();

	public void releasePort(int port) {
		usedPorts.add(port);
		for (Iterator<Integer> it = usedPorts.iterator(); it.hasNext();) {
			int testPort = it.next();
			if (!portUsed(testPort)) {
				it.remove();
				freePorts.add(testPort);
				
				logger.info("Port is free: " + testPort); 
			}
		}
	}

	private boolean portUsed(int port) {
		try {
			ServerSocket socket = new ServerSocket(port);
			socket.close();
			return true;
		} catch (Exception e) {
		}

		return false;
	}

	public synchronized int getPort() {
		// Check if there are any free ports
		if (freePorts.isEmpty())
			return this.currentPort++;

		// Find a free port
		synchronized (freePorts) {
			Integer foundPort = freePorts.poll();
			if (foundPort != null)
				return foundPort;
		}

		return this.currentPort++;
	}
}

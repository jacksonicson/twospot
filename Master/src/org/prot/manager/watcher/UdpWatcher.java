package org.prot.manager.watcher;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.apache.log4j.Logger;

public class UdpWatcher
{
	private static final Logger logger = Logger.getLogger(UdpWatcher.class);

	public UdpWatcher()
	{
		try
		{
			DatagramSocket socket = new DatagramSocket(3232);

			while (true)
			{
				DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
				logger.debug("awaiting udp package");
				socket.receive(packet);
				logger.debug("Received package: " + new String(packet.getData()));

			}

		} catch (SocketException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

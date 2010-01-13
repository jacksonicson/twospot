package org.prot.util.managment;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.apache.log4j.Logger;

public class UdpListener implements Runnable
{
	private static final Logger logger = Logger.getLogger(UdpListener.class);

	private DatagramSocket socket;

	public void init()
	{
		try
		{
			Thread thread = new Thread(this);
			thread.start();
		} catch (Exception e)
		{
			logger.fatal("Error while starting UDP thread", e);
			System.exit(1);
		}
	}

	protected int getPort()
	{
		return 3232;
	}

	protected void handleDatagram(DatagramPacket packet) throws IOException
	{
		logger.debug("Datagram received");
	}

	public void run()
	{
		try
		{
			// TODO: PORT!!!
			this.socket = new DatagramSocket(getPort());
		} catch (SocketException e)
		{
			logger.fatal("Could not create UDP socket", e);
			System.exit(1);
		}

		while (true)
		{
			DatagramPacket packet = new DatagramPacket(new byte[2048], 2048);
			try
			{
				socket.receive(packet);
				handleDatagram(packet);
			} catch (IOException e)
			{
				logger.error("Could not recive datagram", e);
				continue;
			} catch (Exception e)
			{
				logger.error("Unhandled error", e);
				continue;
			}
		}
	}
}
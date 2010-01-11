package org.prot.appserver.management;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.log4j.Logger;
import org.prot.util.managment.gen.ManagementData.AppServer;
import org.prot.util.scheduler.Scheduler;
import org.prot.util.scheduler.SchedulerTask;

public class AppManager
{
	private static final Logger logger = Logger.getLogger(AppManager.class);

	DatagramSocket socket = null;

	private Management managedApp = null;

	public AppManager()
	{
		try
		{
			socket = new DatagramSocket();
			socket.setSoTimeout(3000);
		} catch (SocketException e)
		{
			logger.fatal("Could not create socket", e);
			System.exit(1);
		}

		Scheduler.addTask(new Task());
	}

	public void manage(Management managedApp)
	{
		this.managedApp = managedApp;
	}

	public void update() throws IOException
	{
		if (managedApp == null)
			return;

		// Create the message
		AppServer.Builder builder = AppServer.newBuilder();
		managedApp.fill(builder);

		// Send the message to the controller
		byte[] data = builder.build().toByteArray();
		DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 3232);

		logger.debug("Sending management datagram to the Controller");
		socket.send(packet);
	}

	class Task extends SchedulerTask
	{
		@Override
		public long getInterval()
		{
			return 5000;
		}

		@Override
		public void run()
		{
			try
			{
				update();
			} catch (IOException e)
			{
				logger.error("IOException", e);
			} catch (Exception e)
			{
				logger.fatal("Unhandled exceptino", e);
			}
		}
	}
}

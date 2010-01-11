package org.prot.appserver.management;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.log4j.Logger;
import org.prot.appserver.config.Configuration;
import org.prot.util.managment.gen.ManagementData.AppServer;
import org.prot.util.scheduler.Scheduler;
import org.prot.util.scheduler.SchedulerTask;

public class AppServerManager
{
	private static final Logger logger = Logger.getLogger(AppServerManager.class);

	DatagramSocket socket = null;

	private RuntimeManagement managedApp = null;

	public AppServerManager()
	{
		try
		{
			// Create a new socket which is used to send datagrams to the
			// Controller
			socket = new DatagramSocket();
			socket.setSoTimeout(3000);
		} catch (SocketException e)
		{
			logger.fatal("Could not create socket", e);
			System.exit(1);
		}

		// Task which regularely sends datagrams
		Scheduler.addTask(new Task());
	}

	public void update() throws IOException
	{
		if (managedApp == null)
			return;

		// Create the message
		AppServer.Builder builder = AppServer.newBuilder();
		builder.setAppId(Configuration.getInstance().getAppId());
		managedApp.fill(builder);

		// Create the datagram
		byte[] data = builder.build().toByteArray();
		DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getLocalHost(),
				Configuration.getInstance().getControllerDatagramPort());

		// Send the datagram to the Controlelr
		socket.send(packet);
	}

	class Task extends SchedulerTask
	{
		@Override
		public long getInterval()
		{
			return 3000;
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

	public void manage(RuntimeManagement managedApp)
	{
		this.managedApp = managedApp;
	}
}

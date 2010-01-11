package org.prot.controller.stats;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.log4j.Logger;
import org.prot.util.managment.UdpListener;
import org.prot.util.managment.gen.ManagementData;
import org.prot.util.managment.gen.ManagementData.AppServer;
import org.prot.util.managment.gen.ManagementData.Controller;
import org.prot.util.scheduler.Scheduler;
import org.prot.util.scheduler.SchedulerTask;

public class ControllerManagement extends UdpListener
{
	private static final Logger logger = Logger.getLogger(ControllerManagement.class);

	private ControllerStatsCollector controllerStatsCollector;

	private DatagramSocket socket = null;

	public ControllerManagement()
	{
		// Do nothing
	}
	
	public void init()
	{
		super.init();
		
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

	protected int getPort()
	{
		return 3234;
	}
	
	protected void handleDatagram(DatagramPacket packet) throws IOException
	{
		ByteArrayInputStream in = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
		AppServer appServer = AppServer.parseFrom(in);
		controllerStatsCollector.update(appServer);
	}

	private void update() throws IOException
	{
		ManagementData.Controller.Builder builder = ManagementData.Controller.newBuilder();
		controllerStatsCollector.fill(builder); 

		// Send the message to the controller
		Controller controller = builder.build();
		byte[] data = controller.toByteArray();
		DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 3233);

		logger.debug("Sending management datagram to the Controller: " + data.length);
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

	public void setControllerStatsCollector(ControllerStatsCollector controllerStatsCollector)
	{
		this.controllerStatsCollector = controllerStatsCollector;
	}
}

package org.prot.controller.management;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.prot.util.managment.UdpListener;
import org.prot.util.managment.gen.ManagementData;
import org.prot.util.managment.gen.ManagementData.AppServer;
import org.prot.util.scheduler.Scheduler;
import org.prot.util.scheduler.SchedulerTask;

import com.google.protobuf.InvalidProtocolBufferException;

public class ControllerManagement extends UdpListener
{
	private static final Logger logger = Logger.getLogger(ControllerManagement.class);

	DatagramSocket socket = null;

	private List<AppServer> appServerDatagrams = new LinkedList<AppServer>();

	public ControllerManagement()
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

	protected void handleDatagram(DatagramPacket packet)
	{
		AppServer.Builder builder = AppServer.newBuilder();
		try
		{
			builder.mergeFrom(packet.getData());
		} catch (InvalidProtocolBufferException e)
		{
			logger.error("InvalidProtocolBufferException", e);
			return;
		}

		AppServer appServer = builder.build();
		appServerDatagrams.add(appServer);
	}

	private void update() throws IOException
	{
		ManagementData.Controller.Builder builder = ManagementData.Controller.newBuilder();
		builder.addAllAppServers(appServerDatagrams);
		// TODO: FILL
		
		// Send the message to the controller
		byte[] data = null; // builder.build().toByteArray();
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

	public void todo()
	{
		// appServerWatcher.update();
		//
		// Set<StatsValue> data = new HashSet<StatsValue>();
		//
		// data.add(new DoubleStat(StatType.CPU_USAGE,
		// stats.getControllerStats().getSystemLoadAverage()));
		// data.add(new LongStat(StatType.FREE_MEMORY,
		// stats.getControllerStats().getFrePhysicalMemorySize()));
		// data
		// .add(new LongStat(StatType.TOTAL_MEMORY, stats.getControllerStats()
		// .getTotalPhysicalMemorySize()));
		// data.add(new DoubleStat(StatType.REQUESTS_PER_SECOND,
		// stats.getControllerStats().getRps()));
		//
		// Map<String, Set<StatsValue>> appStats = stats.getAppStats();
		// for (String appId : appStats.keySet())
		// {
		// AppStat stat = new AppStat(StatType.APPLICATION, appId,
		// appStats.get(appId));
		// data.add(stat);
		// }
		//
		// // if (pool instanceof QueuedThreadPool)
		// // {
		// // QueuedThreadPool p2 = (QueuedThreadPool) pool;
		// // logger.warn("IS LOW: " + p2.isLowOnThreads());
		// // logger.warn("WAITING: " + p2.getThreads());
		// // logger.warn("IDLE: " + pool.getIdleThreads());
		// // }
		//
		// //
		//		
		// ManagementData.Controller.Builder builder =
		// ManagementData.Controller.newBuilder();
		// builder.setAddress("");
		//		
		//
		// ManagementData.Controller controller = builder.build();
		// byte[] dt = controller.toByteArray();
		// // TODO: UDP TO Manager
		//
		// return data;
	}
}

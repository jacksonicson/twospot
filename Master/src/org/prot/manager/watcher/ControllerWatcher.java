package org.prot.manager.watcher;

import java.net.DatagramPacket;

import org.apache.log4j.Logger;
import org.prot.manager.stats.ControllerRegistry;
import org.prot.manager.stats.Stats;
import org.prot.util.managment.UdpListener;

public class ControllerWatcher extends UdpListener
{
	private static final Logger logger = Logger.getLogger(ControllerWatcher.class);

	private ControllerRegistry registry;

	private Stats stats;

	protected void handleDatagram(DatagramPacket packet)
	{
		logger.debug("Datagram received");
		// stats.startUpdate();
		//
		// // Iterate over all controllers
		// for (ControllerInfo info : registry.getControllers().values())
		// {
		// try
		// {
		// logger.debug("Querying Controller: " + info.getServiceAddress());
		//
		// // Get JMX connection
		// JmxController connection =
		// getJmxController(info.getServiceAddress());
		// JmxPing ping = connection.getJmxResources();
		// stats.updateController(info.getAddress(), ping);
		//
		// } catch (Exception e)
		// {
		// removeController(info.getServiceAddress());
		// logger.trace("Exception", e);
		// }
		// }
		//
		// stats.finalizeUpdate();
		// stats.dump();
	}
}

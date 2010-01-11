package org.prot.manager.stats;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;

import org.apache.log4j.Logger;
import org.prot.util.managment.UdpListener;
import org.prot.util.managment.gen.ManagementData.Controller;

public class MasterManagement extends UdpListener
{
	private static final Logger logger = Logger.getLogger(MasterManagement.class);

	private ControllerRegistry registry;

	private Stats stats;

	public void init()
	{
		super.init();
	}

	protected int getPort()
	{
		return 3233;
	}

	protected void handleDatagram(DatagramPacket packet) throws IOException
	{
		logger.debug("Datagram received: " + packet.getLength());
		ByteArrayInputStream in = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
		Controller controller = Controller.parseFrom(in);
		logger.debug("Address is: " + controller.getAddress());

		if (registry.getController(controller.getAddress()) == null)
		{
			logger.error("Registry does not contain UDP controller");
			return;
		}

		stats.startUpdate();
		stats.updateController(controller);
		stats.finalizeUpdate();
		stats.dump();
	}

	public void setRegistry(ControllerRegistry registry)
	{
		this.registry = registry;
	}

	public void setStats(Stats stats)
	{
		this.stats = stats;
	}
}

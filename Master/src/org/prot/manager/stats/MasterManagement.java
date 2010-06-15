/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
package org.prot.manager.stats;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;

import org.apache.log4j.Logger;
import org.prot.manager.config.Configuration;
import org.prot.util.managment.UdpListener;
import org.prot.util.managment.gen.ManagementData.Controller;

public class MasterManagement extends UdpListener
{
	private static final Logger logger = Logger.getLogger(MasterManagement.class);

	private ControllerRegistry registry;

	public void init()
	{
		super.init();
	}

	protected int getPort()
	{
		return Configuration.getConfiguration().getMasterDatagramPort();
	}

	protected void handleDatagram(DatagramPacket packet) throws IOException
	{
		logger.debug("== Datagram received: " + packet.getLength());
		ByteArrayInputStream in = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
		Controller controller = Controller.parseFrom(in);

		if (!registry.containsControllerInfo(controller.getAddress()))
		{
			logger.warn("Registry doesn't have a Controller for the Datagram: " + controller.getAddress());
			return;
		}

		registry.updateController(controller);
		registry.finalizeUpdate();
		registry.dump();
	}

	public void setRegistry(ControllerRegistry registry)
	{
		this.registry = registry;
	}
}

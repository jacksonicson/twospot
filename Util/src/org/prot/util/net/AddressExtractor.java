package org.prot.util.net;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;

import org.apache.log4j.Logger;

public final class AddressExtractor
{
	private static final Logger logger = Logger.getLogger(AddressExtractor.class);

	public static InetAddress getInetAddress(String ifName) throws SocketException
	{
		NetworkInterface netInterface = NetworkInterface.getByName(ifName);
		List<InterfaceAddress> addresses = netInterface.getInterfaceAddresses();

		for (InterfaceAddress address : addresses)
		{
			InetAddress inetAddress = address.getAddress();

			logger.debug("InetAddress: " + inetAddress);

			return inetAddress;
		}

		return null;
	}
}

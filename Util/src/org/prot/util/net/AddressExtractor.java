package org.prot.util.net;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

public final class AddressExtractor
{
	private static final Logger logger = Logger.getLogger(AddressExtractor.class);

	public static InetAddress getInetAddress(String ifName, boolean ipv6) throws SocketException
	{
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		for (NetworkInterface networkInterface = interfaces.nextElement(); interfaces.hasMoreElements(); networkInterface = interfaces
				.nextElement())
		{
			logger.info("Interface: " + networkInterface.getName() + " = " + networkInterface.getDisplayName());
		}

		NetworkInterface netInterface = NetworkInterface.getByName(ifName);
		List<InterfaceAddress> addresses = netInterface.getInterfaceAddresses();

		for (InterfaceAddress address : addresses)
		{
			InetAddress inetAddress = address.getAddress();
			
			logger.debug("address: " + inetAddress.getHostAddress() + " length: " + inetAddress.getAddress().length);
			
			if(ipv6 && inetAddress.getAddress().length == 16)
				return inetAddress;
			else if(!ipv6 && inetAddress.getAddress().length == 4)
				return inetAddress;
		}

		throw new SocketException();
	}
}

package org.prot.util.net;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Determines the IPv4 or IPv6 address to a given network interface name
 * 
 * @author Andreas Wolke
 * 
 */
public final class AddressExtractor {
	private static final Logger logger = Logger
			.getLogger(AddressExtractor.class);

	public static InetAddress getInetAddress(String ifName, boolean ipv6)
			throws SocketException {

		// List of all available network interfaces
		Enumeration<NetworkInterface> interfaces = NetworkInterface
				.getNetworkInterfaces();

		// Iterate over all interfaces and log them. This information is useful
		// while debugging and configuring the twospot platform
		for (NetworkInterface networkInterface = interfaces.nextElement(); interfaces
				.hasMoreElements(); networkInterface = interfaces.nextElement()) {

			logger.debug("Network interface: " + networkInterface.getName()
					+ " = " + networkInterface.getDisplayName());
		}

		// Gets the network interface by the given name and loads all addresses
		NetworkInterface netInterface = NetworkInterface.getByName(ifName);
		List<InterfaceAddress> addresses = netInterface.getInterfaceAddresses();

		// Iterates over all addresses
		for (InterfaceAddress address : addresses) {
			InetAddress inetAddress = address.getAddress();

			logger.debug("address: " + inetAddress.getHostAddress()
					+ " length: " + inetAddress.getAddress().length);

			// Check if it matches the required address type
			if (ipv6 && inetAddress.getAddress().length == 16)
				return inetAddress;
			else if (!ipv6 && inetAddress.getAddress().length == 4)
				return inetAddress;
		}

		// Could not find the address
		throw new SocketException();
	}
}

package org.prot.util;

import org.apache.log4j.Logger;

public final class AppIdExtractor
{
	private static final Logger logger = Logger.getLogger(AppIdExtractor.class);

	/**
	 * Extracts the AppId from URL's of the type: scheme://appId.domain:port/xxx
	 * 
	 * @param the
	 *            complete url
	 * @return the extracted appId
	 */
	public static String fromDomain(String url)
	{
		int index = -1;

		// Remove the scheme part
		index = url.indexOf("://");
		if (index != -1)
			url = url.substring(index + 3);

		// Find the first dot and remove everything after it
		index = url.indexOf(".");
		if (index != -1)
			url = url.substring(0, index);

		// Check if the result is a valid AppId
		if (!ReservedAppIds.validateAppId(url))
			return null;

		logger.debug("Extracted AppId: " + url);

		// Return the AppId
		return url;
	}

	/**
	 * Extracts the AppId from URL's of the type: scheme://doamin:port/appId/xxx
	 * 
	 * @param the
	 *            complete url
	 * @return the extracted appId
	 */
	public static String fromUrlPath(String url)
	{
		int index = -1;

		// Remove the scheme part
		index = url.indexOf("://");
		if (index != -1)
			url = url.substring(index + 3);

		return fromUri(url);
	}

	public static String fromUri(String uri)
	{
		logger.debug("Extracting AppId from URI: " + uri);

		int index = -1;

		// Remove the first slash
		if (uri.startsWith("/"))
			uri = uri.substring(1);

		// Extract everything before the next slasth
		index = uri.indexOf("/");
		if (index != -1)
		{
			if (index == 0)
				uri = "";
			else
				uri = uri.substring(0, index);
		}

		// Check if the result is a valid AppId
		if (!ReservedAppIds.validateAppId(uri))
			return null;

		logger.debug("Extracted AppId: " + uri);

		// Return the AppId
		return uri;
	}
}

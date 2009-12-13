package org.prot.util;

public final class AppIdExtractor
{
	/**
	 * Extracts the AppId from URL's of the type: scheme://appId.domain:port/xxx
	 * 
	 * @param the
	 *            complete url
	 * @return the extracted appId
	 */
	public static String fromDomain(String url)
	{
		// Remove the scheme part
		int start = url.indexOf("://");
		if (start == -1)
			return null;
		start += 3;

		// Find the first dot and remove everything after it
		int dest = url.indexOf(".", start);
		if (dest == -1)
			return null;

		// Extract the AppId and check its length
		int length = dest - start;
		if (length >= ReservedAppIds.MIN_LENGTH && length <= ReservedAppIds.MAX_LENGTH)
			return url.substring(start, dest);

		return null;
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
		int index = -1;

		// Remove the first slash
		int startIndex = 0;
		if (uri.startsWith("/"))
			startIndex = 1;

		// Extract everything before the next slash
		index = uri.indexOf("/", startIndex);
		if (index != -1)
		{
			if (index == 0)
				return null;
			else
			{
				int length = index - startIndex;
				if (length >= ReservedAppIds.MIN_LENGTH && length <= ReservedAppIds.MAX_LENGTH)
					return uri.substring(startIndex, index);
			}
		}

		return null;
	}
}

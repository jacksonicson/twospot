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
package org.prot.util;

public final class AppIdExtractor
{
	/**
	 * Extracts the AppId from URL's of the type: scheme://appId.domain:port/*
	 * 
	 * @param the
	 *            complete url
	 * @return the extracted appId
	 */
	public static String fromDomain(String url)
	{
		// Remove the schema part
		int start = url.indexOf("://");
		if (start == -1)
			return null;
		start += 3;

		// Find the first dot and remove everything after it
		int end = url.indexOf(".", start);
		if (end == -1)
			return null;

		// Extract the AppId and check its length
		int length = end - start;
		if (length >= ReservedAppIds.MIN_LENGTH && length <= ReservedAppIds.MAX_LENGTH)
			return url.substring(start, end);

		return null;
	}

	/**
	 * Extracts the AppId from URL's of the type: scheme://domain:port/appId/*
	 * 
	 * @param the
	 *            complete url
	 * @return the extracted appId
	 */
	public static String fromUrlPath(String url)
	{
		int index = -1;

		// Remove the schema part
		index = url.indexOf("://");
		if (index != -1)
			url = url.substring(index + 3);

		return fromUri(url);
	}

	public static String fromUri(String uri)
	{
		int index = -1;

		// Remove the first slash
		int start = 0;
		if (uri.startsWith("/"))
			start = 1;

		// Extract everything before the next slash
		index = uri.indexOf("/", start);
		if (index != -1)
		{
			if (index == 0)
				return null;
			else
			{
				int length = index - start;
				if (length >= ReservedAppIds.MIN_LENGTH && length <= ReservedAppIds.MAX_LENGTH)
					return uri.substring(start, index);
			}
		}

		return null;
	}
}

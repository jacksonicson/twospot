package org.prot.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

public final class ReservedAppIds
{
	private static final Logger logger = Logger.getLogger(ReservedAppIds.class);

	public final static String APP_PORTAL = "portal";

	public final static String FRONTEND_DEPLOY = "deploy";

	private final static Set<String> reservedAppIds = new HashSet<String>();
	static
	{
		reservedAppIds.add(APP_PORTAL);
		reservedAppIds.add(FRONTEND_DEPLOY);
	}

	private final static Set<String> privilegedAppIds = new HashSet<String>();
	static
	{
		privilegedAppIds.add(APP_PORTAL);
	}

	public static boolean isReserved(String appId)
	{
		return reservedAppIds.contains(appId);
	}

	public static boolean isPrivilged(String appId)
	{
		return privilegedAppIds.contains(appId);
	}

	/**
	 * Checks if the AppId is valid <b>and</b> if the AppId is reserved.
	 * 
	 * @see ReservedAppIds#validateAppId(String)
	 * @param appId
	 * @return
	 */
	public static boolean validateNewAppId(String appId)
	{
		// Check if this appId is reserved
		if (ReservedAppIds.isReserved(appId))
			return false;

		return validateAppId(appId);
	}

	/**
	 * Checks if the AppId is valid. This method does <b>not</b> check if the
	 * AppId is reserved!
	 * 
	 * @param appId
	 * @return
	 */
	public static boolean validateAppId(String appId)
	{
		logger.debug("Validating AppId: " + appId);

		// Check the length
		if (appId.length() > 10)
			return false;

		if (appId.length() < 3)
			return false;

		// Check each character of the appId
		char[] chars = appId.toCharArray();
		for (char c : chars)
		{
			if (!Character.isLetterOrDigit(c))
				return false;
		}

		// Everything seems ok
		return true;
	}
}

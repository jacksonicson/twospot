package org.prot.util;

import java.util.HashSet;
import java.util.Set;

public final class ReservedAppIds
{
	public final static String APP_PORTAL = "portal";

	public final static String FRONTEND_DEPLOY = "deploy";

	public final static int MIN_LENGTH = 3;

	public final static int MAX_LENGTH = 10;

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

	public static final boolean isReserved(String appId)
	{
		return reservedAppIds.contains(appId);
	}

	public static final boolean isPrivilged(String appId)
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
	public static final boolean validateNewAppId(String appId)
	{
		// Check if this appId is reserved
		if (ReservedAppIds.isReserved(appId))
			return false;

		if (appId.length() < MIN_LENGTH || appId.length() > MAX_LENGTH)
			return false;

		// Check each character of the appId
		char[] chars = appId.toCharArray();
		for (char c : chars)
		{
			if (!Character.isLetterOrDigit(c))
				return false;
		}

		return true;
	}
}

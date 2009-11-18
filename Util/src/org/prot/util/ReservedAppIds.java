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

	public static boolean isReserved(String appName)
	{
		return reservedAppIds.contains(appName);
	}
	
	public static boolean validateAppId(String appId)
	{
		logger.debug("Validating AppId: " + appId); 
		
		// Check if this appId is reserved
		if(ReservedAppIds.isReserved(appId))
			return false;

		// Check the length 
		if(appId.length() > 10)
			return false;
		
		if(appId.length() < 3)
			return false;
		
		// Check each character of the appId
		char[] chars = appId.toCharArray(); 
		for(char c : chars)
		{
			if(!Character.isLetterOrDigit(c))
				return false; 
		}
		
		// Everything seems ok
		return true; 	
	}
}

package org.prot.appserver.runtime.java.data;

import org.prot.appserver.runtime.java.SessionData;

public interface SessionDao
{
	public boolean exists(String sessionId); 
	
	public SessionData loadSession(String id);
	
	public void saveSession(SessionData sessionData); 
	
	public void updateSession(SessionData sessionData); 

	public void deleteSession(SessionData sessionData);
	
	public boolean isStale(String sessionId, long timestamp);
}

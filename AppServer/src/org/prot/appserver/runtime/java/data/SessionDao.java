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
	
	public void addSessionId(String sessionId); 
	
	public void deleteSessionId(String sessionId);
	
	public boolean existsSessionId(String sessionId); 
}

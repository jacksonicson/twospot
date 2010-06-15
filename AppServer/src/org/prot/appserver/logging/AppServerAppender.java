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
package org.prot.appserver.logging;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class AppServerAppender extends AppenderSkeleton
{
	@Override
	protected void append(LoggingEvent event)
	{
		System.out.println("Append: " + event.getMessage());
	}

	@Override
	public void close()
	{
		System.out.println("Closing appender"); 
	}

	@Override
	public boolean requiresLayout()
	{
		return false;
	}
}

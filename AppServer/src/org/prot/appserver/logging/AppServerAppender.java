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

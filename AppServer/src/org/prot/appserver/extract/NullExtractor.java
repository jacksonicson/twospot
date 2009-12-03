package org.prot.appserver.extract;

import java.io.IOException;

import org.apache.log4j.Logger;

public class NullExtractor implements AppExtractor
{
	private static final Logger logger = Logger.getLogger(NullExtractor.class);

	@Override
	public void extract(byte[] archive, String destPath, String appId) throws IOException
	{
		// Do nothing
	}
}

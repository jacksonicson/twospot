package org.prot.appserver.extract;

import java.io.IOException;

public interface AppExtractor
{
	public void extract(byte[] archive, String destPath, String appId) throws IOException;
}

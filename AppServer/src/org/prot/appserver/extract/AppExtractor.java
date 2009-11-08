package org.prot.appserver.extract;

import java.io.IOException;

public interface AppExtractor
{
	public String extract(byte[] archive, String destPath, String destDir) throws IOException;
}

package org.prot.controller.app.lifecycle.extract;

import java.io.IOException;

public interface AppExtractor
{
	public void extract(byte[] archive, String destPath, String appId) throws IOException;
}

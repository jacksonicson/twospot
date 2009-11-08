package org.prot.appserver.extract;

public interface AppExtractor
{
	public String extract(byte[] archive, String destPath, String destDir); 
}

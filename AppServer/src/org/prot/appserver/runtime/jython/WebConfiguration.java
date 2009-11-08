package org.prot.appserver.runtime.jython;

import org.prot.appserver.app.RuntimeConfiguration;

public class WebConfiguration
{
	private String regExp;
	private String pythonFile; 
	
	public WebConfiguration(String regExp, String pythonFile) {
		this.regExp = regExp; 
		this.pythonFile = pythonFile; 
	}
	
	public String matches(String uri) {
		
		if(uri.matches(regExp)) {
			return this.pythonFile;
		}
		
		// URI does not match
		return null; 
	}
}
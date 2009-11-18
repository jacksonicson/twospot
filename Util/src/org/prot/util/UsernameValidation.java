package org.prot.util;

public final class UsernameValidation
{
	public static final boolean isValid(String username)
	{
		char[] cusername = username.toCharArray(); 
		for(char c : cusername) {
			if(!Character.isLetterOrDigit(c))
				return false;
		}
		
		return true; 
	}
}

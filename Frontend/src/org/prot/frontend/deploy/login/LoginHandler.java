package org.prot.frontend.deploy.login;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.prot.frontend.ReconnectingUserService;
import org.prot.util.ReservedAppIds;
import org.prot.util.UsernameValidation;

public class LoginHandler
{
	private static final Logger logger = Logger.getLogger(LoginHandler.class);

	private ReconnectingUserService service = new ReconnectingUserService();
	
	public String doLogin(String appId, String username, String password) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(password.getBytes());
		String md5 = new String(md.digest());

		boolean valid = true;
		valid &= ReservedAppIds.validateAppId(appId);
		valid &= UsernameValidation.isValid(username);
		
		if(!valid){
			logger.info("validation failed"); 
			return null; // TODO: Error
		}
		
		
		return service.login(appId, username, md5);
	}

}

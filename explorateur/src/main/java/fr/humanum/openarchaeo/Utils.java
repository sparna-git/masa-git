package fr.humanum.openarchaeo;

import org.apache.commons.codec.digest.DigestUtils;

public class Utils {

	public static String hashPassword(String password) {
        return DigestUtils.sha1Hex(password);
    }
	
	
	
}

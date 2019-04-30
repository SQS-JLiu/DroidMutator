package mjava.util;

import java.util.UUID;

public class StringGenerator {

	
	public static String generateRandomString(){
		
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}

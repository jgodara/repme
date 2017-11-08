package jgodara.repme.util;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BASE64Encoder {
	private static final Log logger = LogFactory.getLog(BASE64Encoder.class);
	/**
	* Encodes a string into Base64 format.
	* No blanks or line breaks are inserted.
	* @param s  a String to be encoded.
	* @return   A String with the Base64 encoded data.
	*/
	public static String encodeString (String s) {
	   return Base64.encodeBytes(s.getBytes()); 
	}

	/**
	* Encodes a byte array into Base64 format.
	* No blanks or line breaks are inserted.
	* @param in  an array containing the data bytes to be encoded.
	* @return    A character array with the Base64 encoded data.
	*/
	public static char[] encode (byte[] in) {
		return (Base64.encodeBytes(in)).toCharArray();
	}

	/**
	* Decodes a string from Base64 format.
	* @param s  a Base64 String to be decoded.
	* @return   A String containing the decoded data.
	* @throws   IllegalArgumentException if the input is not valid Base64 encoded data.
	*/
	public static String decodeString (String s) {
	   try {
		   return new String(Base64.decode(s));
		} catch (IOException e) {
			logger.error("Base64 Error", e);
			return null;
		} 
	}

	/**
	* Decodes a byte array from Base64 format.
	* @param s  a Base64 String to be decoded.
	* @return   An array containing the decoded data bytes.
	* @throws   IllegalArgumentException if the input is not valid Base64 encoded data.
	*/
	public static byte[] decode (String s) {
	   try {
		   return Base64.decode(s);
		} catch (IOException e) {
			logger.error("Base64 Error", e);
			return null;
		}
	}

	/**
	* Decodes a byte array from Base64 format.
	* No blanks or line breaks are allowed within the Base64 encoded data.
	* @param in  a character array containing the Base64 encoded data.
	* @return    An array containing the decoded data bytes.
	* @throws    IllegalArgumentException if the input is not valid Base64 encoded data.
	*/
	public static byte[] decode (char[] in) {
		try {
		    return Base64.decode(new String(in));
	    } catch (IOException e) {
			logger.error("Base64 Error", e);
			return null;
	    }
	}


}
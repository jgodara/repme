package jgodara.repme.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.io.IOException;

import org.springframework.util.StringUtils;

public class StringUtil {
	public static final String DEFAULT_CHAR_CODE = "utf8";

	private StringUtil() {
		// nothing
	}

	public static boolean isBlank(String s) {
		return !StringUtils.hasText(s);
	}

	public static boolean isEmpty(String s) {
		return !StringUtils.hasLength(s);
	}

	public static boolean notEmpty(String s) {
		return StringUtils.hasLength(s);
	}

	public static String encodeUrl(String s)
			throws UnsupportedEncodingException {
		return URLEncoder.encode(s, DEFAULT_CHAR_CODE);
	}

	public static String decodeUrl(String s)
			throws UnsupportedEncodingException {
		return URLDecoder.decode(s, DEFAULT_CHAR_CODE);
	}

	public static String base64Decode(String code) throws IOException {
		return BASE64Encoder.decodeString(code);
	}

	public static String base64Encode(String source) {
		return BASE64Encoder.encodeString(source);
	}

	public static boolean isPattern(String pattern) {
		return (pattern.indexOf('*') != -1 || pattern.indexOf('?') != -1);
	}

	public static String appendUri(String uri, String appendQuery) {
		try {
			URI oldUri = new URI(uri);
	
			String newQuery = oldUri.getQuery();
			if (newQuery == null) {
				newQuery = appendQuery;
			} else {
				newQuery += "&" + appendQuery;
			}
	
			URI newUri = new URI(oldUri.getScheme(), oldUri.getAuthority(),
					oldUri.getPath(), newQuery, oldUri.getFragment());
	
			return newUri.toString();
		} catch (Exception ex) {
			return uri;
		}
	}

	public static boolean matchStrings(String pattern, String str) {
		char[] patArr = pattern.toCharArray();
		char[] strArr = str.toCharArray();
		int patIdxStart = 0;
		int patIdxEnd = patArr.length - 1;
		int strIdxStart = 0;
		int strIdxEnd = strArr.length - 1;
		char ch;

		boolean containsStar = false;
		for (int i = 0; i < patArr.length; i++) {
			if (patArr[i] == '*') {
				containsStar = true;
				break;
			}
		}

		if (!containsStar) {
			// No '*'s, so we make a shortcut
			if (patIdxEnd != strIdxEnd) {
				return false; // Pattern and string do not have the same size
			}
			for (int i = 0; i <= patIdxEnd; i++) {
				ch = patArr[i];
				if (ch != '?') {
					if (ch != strArr[i]) {
						return false;// Character mismatch
					}
				}
			}
			return true; // String matches against pattern
		}

		if (patIdxEnd == 0) {
			return true; // Pattern contains only '*', which matches anything
		}

		// Process characters before first star
		while ((ch = patArr[patIdxStart]) != '*' && strIdxStart <= strIdxEnd) {
			if (ch != '?') {
				if (ch != strArr[strIdxStart]) {
					return false;// Character mismatch
				}
			}
			patIdxStart++;
			strIdxStart++;
		}
		if (strIdxStart > strIdxEnd) {
			// All characters in the string are used. Check if only '*'s are
			// left in the pattern. If so, we succeeded. Otherwise failure.
			for (int i = patIdxStart; i <= patIdxEnd; i++) {
				if (patArr[i] != '*') {
					return false;
				}
			}
			return true;
		}

		// Process characters after last star
		while ((ch = patArr[patIdxEnd]) != '*' && strIdxStart <= strIdxEnd) {
			if (ch != '?') {
				if (ch != strArr[strIdxEnd]) {
					return false;// Character mismatch
				}
			}
			patIdxEnd--;
			strIdxEnd--;
		}
		if (strIdxStart > strIdxEnd) {
			// All characters in the string are used. Check if only '*'s are
			// left in the pattern. If so, we succeeded. Otherwise failure.
			for (int i = patIdxStart; i <= patIdxEnd; i++) {
				if (patArr[i] != '*') {
					return false;
				}
			}
			return true;
		}

		// process pattern between stars. padIdxStart and patIdxEnd point
		// always to a '*'.
		while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
			int patIdxTmp = -1;
			for (int i = patIdxStart + 1; i <= patIdxEnd; i++) {
				if (patArr[i] == '*') {
					patIdxTmp = i;
					break;
				}
			}
			if (patIdxTmp == patIdxStart + 1) {
				// Two stars next to each other, skip the first one.
				patIdxStart++;
				continue;
			}
			// Find the pattern between padIdxStart & padIdxTmp in str between
			// strIdxStart & strIdxEnd
			int patLength = (patIdxTmp - patIdxStart - 1);
			int strLength = (strIdxEnd - strIdxStart + 1);
			int foundIdx = -1;
			strLoop: for (int i = 0; i <= strLength - patLength; i++) {
				for (int j = 0; j < patLength; j++) {
					ch = patArr[patIdxStart + j + 1];
					if (ch != '?') {
						if (ch != strArr[strIdxStart + i + j]) {
							continue strLoop;
						}
					}
				}

				foundIdx = strIdxStart + i;
				break;
			}

			if (foundIdx == -1) {
				return false;
			}

			patIdxStart = patIdxTmp;
			strIdxStart = foundIdx + patLength;
		}

		// All characters in the string are used. Check if only '*'s are left
		// in the pattern. If so, we succeeded. Otherwise failure.
		for (int i = patIdxStart; i <= patIdxEnd; i++) {
			if (patArr[i] != '*') {
				return false;
			}
		}

		return true;
	}

	public static void main(String[] args) {
	}

}
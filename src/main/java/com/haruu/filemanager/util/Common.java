package com.haruu.filemanager.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Common {

	public static String rawUrlEncode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8")
					.replace("*", "%2A")
					.replace("+", "%20")
					.replace("%7E", "~");
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}

}

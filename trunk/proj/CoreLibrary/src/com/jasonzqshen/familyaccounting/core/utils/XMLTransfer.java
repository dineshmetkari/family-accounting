package com.jasonzqshen.familyaccounting.core.utils;

/**
 * Transfer XML document to string
 * 
 * @author I072485
 * 
 */
public class XMLTransfer {
	private XMLTransfer() {
	}

	
	public static final String BEGIN_TAG_LEFT = "<";
	public static final String BEGIN_TAG_RIGHT = ">\n";
	public static final String END_TAG_LEFT = "</";
	public static final String END_TAG_RIGHT = ">\n";
	public static final String SINGLE_TAG_LEFT = "<";
	public static final String SINGLE_TAG_RIGHT = "/>\n";
	
	public static final String char_amp = "&amp;";
	public static final String char_apos = "&apos;";
	public static final String char_quot = "&quot;";

	public static boolean validCharacters(String str) {
		return true;
	}
	
	public static String toSafeString(String str) {
		
		String rs = str;
		
		rs = rs.replaceAll("&", char_amp);
		rs = rs.replaceAll("'", char_apos);
		rs = rs.replaceAll("\"", char_quot);		
		
		return rs;
		
	}
}

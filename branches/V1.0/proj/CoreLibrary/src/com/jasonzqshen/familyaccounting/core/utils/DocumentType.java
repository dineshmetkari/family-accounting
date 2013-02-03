package com.jasonzqshen.familyaccounting.core.utils;

public enum DocumentType {
	GL('S'), VENDOR_INVOICE('K'), CUSTOMER_INVOICE('D');

	private final char _value;

	private DocumentType(char ch) {
		_value = ch;
	}

	@Override
	public String toString() {
		return String.valueOf(_value);
	}

	/**
	 * 
	 * @param ch
	 * @return
	 */
	public static DocumentType parse(char ch) {
		switch (ch) {
		case 'S':
			return DocumentType.GL;
		case 'K':
			return DocumentType.VENDOR_INVOICE;
		case 'D':
			return DocumentType.CUSTOMER_INVOICE;
		}
		return null;
		
	}
}

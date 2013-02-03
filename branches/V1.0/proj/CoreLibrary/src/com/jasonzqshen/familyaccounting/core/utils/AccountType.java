package com.jasonzqshen.familyaccounting.core.utils;

public enum AccountType {
	GL_ACCOUNT('S'), VENDOR('K'), CUSTOMER('D');

	
	public final char _value;

	private AccountType(char ch) {
		_value = ch;
	}

	@Override
	public String toString() {
		return String.valueOf(_value);
	}

	/**
	 * parse value to account type
	 * 
	 * @param ch
	 * @return
	 */
	public static AccountType parse(char ch) {
		switch (ch) {
		case 'S':
			return AccountType.GL_ACCOUNT;
		case 'D':
			return AccountType.CUSTOMER;
		case 'K':
			return AccountType.VENDOR;
		}

		return null;
	}
}

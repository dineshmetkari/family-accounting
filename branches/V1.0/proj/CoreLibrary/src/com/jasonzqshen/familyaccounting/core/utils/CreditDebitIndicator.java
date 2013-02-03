package com.jasonzqshen.familyaccounting.core.utils;

public enum CreditDebitIndicator {
	CREDIT('C'), DEBIT('D');

	
	public final char _value;

	private CreditDebitIndicator(char ch) {
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
	public static CreditDebitIndicator parse(char ch) {
		switch (ch) {
		case 'C':
			return CreditDebitIndicator.CREDIT;
		case 'D':
			return CreditDebitIndicator.DEBIT;
		}

		return null;
	}
}

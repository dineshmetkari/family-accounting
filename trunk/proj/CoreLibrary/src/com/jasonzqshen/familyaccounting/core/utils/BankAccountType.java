package com.jasonzqshen.familyaccounting.core.utils;

/**
 * Critial level describes the importance and neccessarily of the expence.
 * 
 * 1. High(ID: H) 2. Medium(ID: M) 3. Low(ID: L)
 * 
 * @author I072485
 * 
 */
public enum BankAccountType {
	SAVING_ACCOUNT('C'), DEBIT_CARD('D');

	private char _value;

	/**
	 * constructor
	 * 
	 * @param ch
	 */
	private BankAccountType(char ch) {
		_value = ch;
	}

	@Override
	public String toString() {
		return String.valueOf(_value);
	}

	/**
	 * get value
	 * 
	 * @return value
	 */
	public char getValue() {
		return _value;
	}

	public static BankAccountType parse(char ch) {
		for (BankAccountType t : BankAccountType.values()) {
			if (t.toString().equals(String.valueOf(ch))) {
				return t;
			}
		}
		return null;
	}
}

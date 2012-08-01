package com.jasonzqshen.familyaccounting.core.utils;

/**
 * Critial level describes the importance and neccessarily of the expence.
 * 
 * 1. High(ID: H) 2. Medium(ID: M) 3. Low(ID: L)
 * 
 * @author I072485
 * 
 */
public enum GLAccountType {
	BALANCE('B'), PROFIT_LOSS('P');

	private char _value;

	/**
	 * constructor
	 * 
	 * @param ch
	 */
	private GLAccountType(char ch) {
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

	public static GLAccountType parse(char ch) {
		switch (ch) {
		case 'B':
			return GLAccountType.BALANCE;
		case 'P':
			return GLAccountType.PROFIT_LOSS;
		}
		return null;
	}
}

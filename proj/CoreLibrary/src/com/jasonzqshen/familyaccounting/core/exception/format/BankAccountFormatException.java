package com.jasonzqshen.familyaccounting.core.exception.format;

public class BankAccountFormatException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5657654305017304475L;

	public BankAccountFormatException(String value) {
		super(value + " cannot be parse to BankAccount");
	}
}

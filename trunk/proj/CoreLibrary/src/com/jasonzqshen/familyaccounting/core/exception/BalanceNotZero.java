package com.jasonzqshen.familyaccounting.core.exception;

public class BalanceNotZero extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4589373504194478207L;

	public BalanceNotZero() {
		super("Balance is not zero.");
	}

}

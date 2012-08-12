package com.jasonzqshen.familyaccounting.core.exception;

public class IdentityInvalidChar extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5028568282406271450L;

	public IdentityInvalidChar(char errorCh) {
		super(
				String.format(
						"Master data identity cannot create with invalid charactor %c.",
						errorCh));
	}
}

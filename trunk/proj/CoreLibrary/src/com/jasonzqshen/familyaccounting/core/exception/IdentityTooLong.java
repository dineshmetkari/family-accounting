package com.jasonzqshen.familyaccounting.core.exception;

public class IdentityTooLong extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1241326352948137528L;

	public IdentityTooLong(int actLen, int expLen) {
		super(String.format("The length should short than %d, but it is %d",
				expLen, actLen));
	}
}

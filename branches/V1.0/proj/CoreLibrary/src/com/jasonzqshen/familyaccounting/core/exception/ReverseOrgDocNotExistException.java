package com.jasonzqshen.familyaccounting.core.exception;

public class ReverseOrgDocNotExistException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4377933257679741211L;

	public ReverseOrgDocNotExistException() {
		super("Document to be reversed does not exist");
	}
}

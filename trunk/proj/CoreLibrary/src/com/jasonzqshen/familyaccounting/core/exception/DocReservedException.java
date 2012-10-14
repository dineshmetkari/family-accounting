package com.jasonzqshen.familyaccounting.core.exception;

public class DocReservedException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2168800139866243287L;

	public DocReservedException() {
		super("Document has been reserved before.");
	}
}

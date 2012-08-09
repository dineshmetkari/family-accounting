package com.jasonzqshen.familyaccounting.core.exception;

public class NoFieldNameException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8358659959308638537L;

	public NoFieldNameException(String fieldName) {
		super("No such field " + fieldName);
	}
}

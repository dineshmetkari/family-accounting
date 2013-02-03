package com.jasonzqshen.familyaccounting.core.exception.format;

public class GLAccountGroupFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6376849800223121273L;

	public GLAccountGroupFormatException(String value) {
		super(value + " cannot be parse to GLAccountGroup");
	}
}

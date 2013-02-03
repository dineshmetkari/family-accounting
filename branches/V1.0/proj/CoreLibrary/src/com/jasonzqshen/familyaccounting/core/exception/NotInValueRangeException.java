package com.jasonzqshen.familyaccounting.core.exception;

public class NotInValueRangeException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7365572226472484978L;

	public NotInValueRangeException(String fieldName, Object object) {
		super(String.format("Value %s is not in value range %s.",
				object.toString(), fieldName));
	}
}

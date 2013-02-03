package com.jasonzqshen.familyaccounting.core.exception;

public class NullValueNotAcceptable extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7814804242103529420L;

	/**
	 * field name
	 * 
	 * @param fieldName
	 */
	public NullValueNotAcceptable(String fieldName) {
		super(String.format("Field %s is mandatory. Null is not acceptable.",
				fieldName));
		
	}
}

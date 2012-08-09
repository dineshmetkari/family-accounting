package com.jasonzqshen.familyaccounting.core.exception;

public class MandatoryFieldIsMissing extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 608695170918084879L;
	public final String _fieldName;

	public MandatoryFieldIsMissing(String fieldName) {
		super(String.format("Mandatory field %s is missing", fieldName));
		_fieldName = fieldName;
	}
}

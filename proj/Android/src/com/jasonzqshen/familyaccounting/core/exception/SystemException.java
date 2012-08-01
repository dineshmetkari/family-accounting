package com.jasonzqshen.familyaccounting.core.exception;

public class SystemException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5863824817573111815L;
	public final Exception _exp;

	public SystemException(Exception exp) {
		super(exp.getMessage());
		_exp = exp;
	}
}

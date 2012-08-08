package com.jasonzqshen.familyaccounting.core.exception.runtime;

public class SystemException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5863824817573111815L;
	public final Exception _exp;

	public SystemException(Exception exp) {
		super(exp.getMessage());
		_exp = exp;
	}

	@Override
	public String toString() {
		if (_exp == null) {
			return "";
		}
		return _exp.toString();
	}
}

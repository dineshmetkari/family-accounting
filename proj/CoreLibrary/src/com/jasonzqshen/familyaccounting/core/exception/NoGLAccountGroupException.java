package com.jasonzqshen.familyaccounting.core.exception;

public class NoGLAccountGroupException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8270078634384672829L;

	public NoGLAccountGroupException(String groupID) {
		super(String
				.format("The G/L account group %s does not exist.", groupID));
	}
}

package com.jasonzqshen.familyaccounting.core.exception.format;

public class DocumentIdentityFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4457872286904353350L;

	
	public DocumentIdentityFormatException(String docIdStr) {
		super(String.format("Parsing document Identity \"%s\" contains error.",
				docIdStr));
	}
}

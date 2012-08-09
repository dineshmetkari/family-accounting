package com.jasonzqshen.familyaccounting.core.exception;

public class TransactionDataFileFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final String FILE_PATH;

	public TransactionDataFileFormatException(String filePath) {
		super(String.format("Transaction data file %s contains format error",
				filePath));
		FILE_PATH = filePath;
	}
}

package com.jasonzqshen.familyaccounting.core.exception;

public class NoTransactionDataFileException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7787046276282971224L;
	public final String FILE_PATH;

	
	/**
	 * 
	 * @param type
	 * @param filePath
	 */
	public NoTransactionDataFileException(String filePath) {
		super(String.format("Transaction Data file %s does not exist.",
				filePath));

		FILE_PATH = filePath;

	}
}

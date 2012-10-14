package com.jasonzqshen.familyaccounting.core.exception;

public class SaveClosedLedgerException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1344573935915892788L;

	public SaveClosedLedgerException() {
		super("Cannot save document or modify document in open ledger.");
	}
}

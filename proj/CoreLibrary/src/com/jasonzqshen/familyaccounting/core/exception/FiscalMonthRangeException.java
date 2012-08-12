package com.jasonzqshen.familyaccounting.core.exception;

public class FiscalMonthRangeException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5434654351452909152L;

	
	public FiscalMonthRangeException(int fiscalMonth) {
		super(String.format("Fiscal Year range is from 1 to 12. But it is %d.",
				fiscalMonth));
	}
}

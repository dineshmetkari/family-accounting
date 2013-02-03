package com.jasonzqshen.familyaccounting.core.exception;

public class FiscalYearRangeException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6650231174034410258L;

	
	public FiscalYearRangeException(int fiscalYear) {
		super(String.format(
				"Fiscal Year range is from 1000 to 9999. But the value is %d",
				fiscalYear));
	}
}

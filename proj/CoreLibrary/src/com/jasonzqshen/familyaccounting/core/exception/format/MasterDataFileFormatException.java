package com.jasonzqshen.familyaccounting.core.exception.format;

import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;

public class MasterDataFileFormatException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final MasterDataType TYPE;

	public MasterDataFileFormatException(MasterDataType type) {
		super(String.format(
				"Master data file for master data %s contains format error",
				type));
		TYPE = type;
	}
}

package com.jasonzqshen.familyaccounting.core.exception;

import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;

public class NoMasterDataFileException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7787046276282971224L;
	public final MasterDataType TYPE;
	public final String FILE_PATH;

	/**
	 * 
	 * @param type
	 * @param filePath
	 */
	public NoMasterDataFileException(MasterDataType type, String filePath) {
		super(String.format(
				"Master Data file %s for master data %s does not exist.",
				filePath, type));

		TYPE = type;
		FILE_PATH = filePath;

	}
}

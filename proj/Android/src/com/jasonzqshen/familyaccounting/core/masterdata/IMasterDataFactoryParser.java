package com.jasonzqshen.familyaccounting.core.masterdata;

import com.jasonzqshen.familyaccounting.core.CoreDriver;

public interface IMasterDataFactoryParser {
	/**
	 * Parse XML to memory
	 * 
	 * @param coreDriver
	 * @param elem
	 * @return
	 */
	MasterDataFactoryBase parse(CoreDriver coreDriver);
}

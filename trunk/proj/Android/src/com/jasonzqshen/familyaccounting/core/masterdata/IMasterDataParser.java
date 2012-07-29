package com.jasonzqshen.familyaccounting.core.masterdata;

import org.w3c.dom.Element;

import com.jasonzqshen.familyaccounting.core.CoreDriver;


/**
 * Parse the XML to memory data
 * 
 * @author I072485
 * 
 */
public interface IMasterDataParser {
	MasterDataBase parse(CoreDriver coreDriver,Element elem) throws Exception;
}

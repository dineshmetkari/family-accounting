package com.jasonzqshen.familyaccounting.core.masterdata;

import java.util.ArrayList;

import org.w3c.dom.Element;

/**
 * Parse the XML to memory data
 * 
 * @author I072485
 * 
 */
public interface IMasterDataParser {
	MasterDataBase parse(Element elem, ArrayList<String> errorMsg);
}

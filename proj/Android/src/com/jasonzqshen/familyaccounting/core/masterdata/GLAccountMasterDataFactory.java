package com.jasonzqshen.familyaccounting.core.masterdata;

import org.w3c.dom.Element;

import com.jasonzqshen.familyaccounting.core.CoreDriver;

public class GLAccountMasterDataFactory extends MasterDataFactoryBase {
	/**
	 * factory parser
	 */
	public static final IMasterDataFactoryParser _PARSER = new IMasterDataFactoryParser() {
		public MasterDataFactoryBase parse(CoreDriver coreDriver) {

			return null;
		}
	};

	/**
	 * 
	 * @param parser
	 * @param coreDriver
	 */
	public GLAccountMasterDataFactory(CoreDriver coreDriver) {
		super(_PARSER, coreDriver);
	}

	@Override
	public MasterDataBase createNewMasterDataBase(MasterDataIdentity id) {
		// check id is G/L identity
		return null;
	}

}

package com.jasonzqshen.familyaccounting.core.masterdata;

import java.util.ArrayList;

import com.jasonzqshen.familyaccounting.core.CoreDriver;

public class MasterDataManagement {
	private final CoreDriver _coreDriver;
	private final ArrayList<MasterDataFactoryBase> _factoryList;
	
	public MasterDataManagement(CoreDriver coreDriver) {
		_coreDriver = coreDriver;
		_factoryList = new ArrayList<MasterDataFactoryBase>();
	}
}

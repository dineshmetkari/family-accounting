package com.jasonzqshen.familyaccounting.core.listeners;

import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataFactoryBase;

public interface CreateMasterDataListener {
	void onCreateMasterDataListener(MasterDataFactoryBase factory, MasterDataBase master);
}

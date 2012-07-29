package com.jasonzqshen.familyaccounting.core.masterdata;

import java.util.Hashtable;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;

/**
 * Master Data Management, which contains all master data factories.
 * 
 * @author I072485
 * 
 */
public class MasterDataManagement {
	private final CoreDriver _coreDriver;
	private final Hashtable<MasterDataType, MasterDataFactoryBase> _factoryList;

	/**
	 * 
	 * @param coreDriver
	 */
	public MasterDataManagement(CoreDriver coreDriver) {
		_coreDriver = coreDriver;
		_factoryList = new Hashtable<MasterDataType, MasterDataFactoryBase>();
	}

	/**
	 * initialize the master data; load the data from file system
	 */
	public void init() {

	}

	/**
	 * get master data factory
	 * 
	 * @param type
	 *            master data type
	 * @return master data factory
	 */
	public MasterDataFactoryBase getMasterDataFactory(MasterDataType type) {
		return this._factoryList.get(type);
	}

	/**
	 * get the entity of master data
	 * 
	 * @param idStr
	 *            identity of master data
	 * @param type
	 *            type of master data
	 * @return master data entity
	 * @throws IdentityInvalidChar
	 * @throws IdentityNoData
	 * @throws IdentityTooLong
	 */
	public MasterDataBase getMasterData(char[] idStr, MasterDataType type)
			throws IdentityTooLong, IdentityNoData, IdentityInvalidChar {
		MasterDataFactoryBase factory = this.getMasterDataFactory(type);
		MasterDataIdentity id = new MasterDataIdentity(idStr);

		return factory.getEntity(id);
	}

	/**
	 * 
	 * @param id
	 * @param type
	 * @return
	 * 
	 */
	public MasterDataBase getMasterData(MasterDataIdentity id,
			MasterDataType type) {
		MasterDataFactoryBase factory = this.getMasterDataFactory(type);
		return factory.getEntity(id);
	}
}

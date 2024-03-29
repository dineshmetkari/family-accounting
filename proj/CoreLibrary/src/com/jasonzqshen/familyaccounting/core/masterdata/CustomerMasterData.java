package com.jasonzqshen.familyaccounting.core.masterdata;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;

public class CustomerMasterData extends MasterDataBase {
	public static final String FILE_NAME = "customer.xml";

	/**
	 * 
	 * @param id
	 * @param descp
	 * @param parser
	 * @throws NullValueNotAcceptable 
	 */
	public CustomerMasterData(CoreDriver coreDriver, MasterDataManagement management, MasterDataIdentity id,
			String descp) throws NullValueNotAcceptable {
		super(coreDriver, management, id, descp);
	}
}

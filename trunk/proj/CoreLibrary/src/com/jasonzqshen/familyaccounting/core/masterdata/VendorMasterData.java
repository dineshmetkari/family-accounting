package com.jasonzqshen.familyaccounting.core.masterdata;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;

public class VendorMasterData extends MasterDataBase {
	public static final String FILE_NAME = "vendor.xml";

	/**
	 * 
	 * @param id
	 * @param descp
	 * @param parser
	 * @throws NullValueNotAcceptable 
	 */
	public VendorMasterData(CoreDriver coreDriver, MasterDataIdentity id,
			String descp) throws NullValueNotAcceptable {
		super(coreDriver, id, descp);
	}
}

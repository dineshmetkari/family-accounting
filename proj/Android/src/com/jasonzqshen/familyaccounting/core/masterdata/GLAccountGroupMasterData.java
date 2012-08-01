package com.jasonzqshen.familyaccounting.core.masterdata;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;

public class GLAccountGroupMasterData extends MasterDataBase {
	public static final String FILE_NAME = "gl_account_group.xml";

	/**
	 * 
	 * @param id
	 * @param descp
	 * @param parser
	 * @throws NullValueNotAcceptable 
	 */
	public GLAccountGroupMasterData(CoreDriver coreDriver,
			MasterDataIdentity id, String descp) throws NullValueNotAcceptable {
		super(coreDriver, id, descp);
	}
}

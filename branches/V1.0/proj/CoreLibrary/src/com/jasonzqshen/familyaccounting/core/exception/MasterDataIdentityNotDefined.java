package com.jasonzqshen.familyaccounting.core.exception;

import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;

public class MasterDataIdentityNotDefined extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1012865754518579084L;
	private final MasterDataIdentity _identity;
	private final MasterDataType _type;

	public MasterDataIdentityNotDefined(MasterDataIdentity id,
			MasterDataType type) {
		super(String.format("Bank Key %s is not defined %s", id.toString(),
				type.toString()));

		_identity = id;
		_type = type;
	}

	/**
	 * get master data identity
	 * 
	 * @return
	 */
	public MasterDataIdentity getIdentity() {
		return _identity;
	}

	/**
	 * get master data type
	 * 
	 * @return
	 */
	public MasterDataType getMasterDataType() {
		return _type;
	}
}

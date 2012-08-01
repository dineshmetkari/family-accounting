package com.jasonzqshen.familyaccounting.core.exception;

import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;

public class NoMasterDataFactoryClass extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9134563712030765335L;

	public final MasterDataType _type;

	public NoMasterDataFactoryClass(MasterDataType type) {
		super(String.format("Master data type %s is not registered",
				type.toString()));

		_type = type;

	}

}

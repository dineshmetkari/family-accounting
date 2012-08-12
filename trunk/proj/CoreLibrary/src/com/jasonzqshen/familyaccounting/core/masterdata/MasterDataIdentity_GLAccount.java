package com.jasonzqshen.familyaccounting.core.masterdata;

import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;

/**
 * The identity only within numbers
 * 
 * @author I072485
 * 
 */
public class MasterDataIdentity_GLAccount extends MasterDataIdentity {
	public MasterDataIdentity_GLAccount(String id) throws IdentityTooLong,
			IdentityNoData, IdentityInvalidChar {
		super(id);
	}

	public MasterDataIdentity_GLAccount(char[] id) throws IdentityTooLong,
			IdentityNoData, IdentityInvalidChar {
		super(id);
	}

	/**
	 * only number
	 * 
	 * @param ch
	 * @return
	 */
	@Override
	protected boolean isValidChar(char ch) {
		if ('0' <= ch && ch <= '9') {
			return true;
		}
		return false;
	}

}

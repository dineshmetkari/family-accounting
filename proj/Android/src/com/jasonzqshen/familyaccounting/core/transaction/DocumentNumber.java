package com.jasonzqshen.familyaccounting.core.transaction;

import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;

public class DocumentNumber extends MasterDataIdentity_GLAccount {

	/**
	 * document number
	 * 
	 * @param id
	 * @throws IdentityTooLong
	 * @throws IdentityNoData
	 * @throws IdentityInvalidChar
	 */
	public DocumentNumber(char[] id) throws IdentityTooLong, IdentityNoData,
			IdentityInvalidChar {
		super(id);
	}
}

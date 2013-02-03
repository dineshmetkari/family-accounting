package com.jasonzqshen.familyaccounting.core.transaction;

import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;

public class DocumentNumber extends MasterDataIdentity_GLAccount {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6794864755009825638L;

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

	/**
	 * get next document number
	 * 
	 * @return
	 */
	public DocumentNumber next() {
		char[] newId = this.toString().toCharArray();

		for (int i = newId.length - 1; i >= 0; --i) {
			newId[i]++;
			if (newId[i] > '9') {
				newId[i] = '0';
			} else {
				break;
			}
		}

		try {
			return new DocumentNumber(newId);
		} catch (IdentityTooLong e) {
			e.printStackTrace();
		} catch (IdentityNoData e) {
			e.printStackTrace();
		} catch (IdentityInvalidChar e) {
			e.printStackTrace();
		}
		return null;
	}
}

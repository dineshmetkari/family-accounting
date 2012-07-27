package com.jasonzqshen.familyaccounting.core.masterdata;

import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;

/**
 * the length of the identity is 10. if the input identity is less than 10,
 * right align. And then fill the leading '0'. For example, 00IDENTITY
 * 
 * @author I072485
 * 
 */
public class MasterDataIdentity {
	public final static int LENGTH = 10;
	private final char[] _identity = new char[LENGTH];

	/**
	 * Construct identity
	 * 
	 * @param id
	 * @throws IdentityTooLong
	 * @throws IdentityNoData
	 * @throws IdentityInvalidChar
	 */
	public MasterDataIdentity(char[] id) throws IdentityTooLong,
			IdentityNoData, IdentityInvalidChar {
		boolean containData = false;
		if (id.length == 0) {
			throw new IdentityNoData();
		}
		if (id.length > LENGTH) {
			throw new IdentityTooLong();
		}

		int l = id.length;
		for (int i = LENGTH - 1; i >= 0; i--) {
			if (l + i - LENGTH >= 0) {
				// check character valid
				boolean flag = isValidChar(id[l + i - LENGTH]);
				if (flag == false) {
					throw new IdentityInvalidChar();
				}

				// check whether contains data
				if (id[l + i - LENGTH] != '0') {
					containData = true;
				}

				_identity[i] = Character.toUpperCase(id[l + i - LENGTH]);
			} else {
				// leading zero
				_identity[i] = '0';
			}
		}

		if (!containData) {
			throw new IdentityNoData();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MasterDataIdentity)) {
			return false;
		}
		MasterDataIdentity id = (MasterDataIdentity) obj;
		for (int i = 0; i < LENGTH; ++i) {
			if (id._identity[i] != _identity[i]) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		int sum = 0;
		for (int i = 0; i < LENGTH; ++i) {
			sum += _identity[i];
		}
		return sum;
	}

	/**
	 * to String
	 */
	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		for (char ch : _identity) {
			strBuilder.append(ch);
		}
		return strBuilder.toString();
	}

	/**
	 * only number, char(a..z) and '_' is valid
	 * 
	 * @param ch
	 * @return
	 */
	protected boolean isValidChar(char ch) {
		if (('0' <= ch && ch <= '9') || ('a' <= ch && ch <= 'z')
				|| ('A' <= ch && ch <= 'Z') || ch == '_') {
			return true;
		}
		return false;
	}

}

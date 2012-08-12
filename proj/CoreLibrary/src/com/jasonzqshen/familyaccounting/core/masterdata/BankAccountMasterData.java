package com.jasonzqshen.familyaccounting.core.masterdata;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.utils.BankAccountType;

public class BankAccountMasterData extends MasterDataBase {
	public static final String FILE_NAME = "bank_account.xml";

	/**
	 * bank account number
	 */
	private BankAccountNumber _accNumber;
	/**
	 * bank key
	 */
	private MasterDataIdentity _bankKey;
	/**
	 * bank account type
	 */
	private BankAccountType _bankAccType;

	/**
	 * 
	 * @param id
	 * @param descp
	 * @param parser
	 * @throws MasterDataIdentityNotDefined
	 * @throws NullValueNotAcceptable
	 */
	public BankAccountMasterData(CoreDriver coreDriver, MasterDataManagement management,
			MasterDataIdentity id, String descp, BankAccountNumber accNumber,
			MasterDataIdentity bankKey, BankAccountType type)
			throws MasterDataIdentityNotDefined, NullValueNotAcceptable {
		super(coreDriver, management, id, descp);

		_accNumber = accNumber;
		_bankAccType = type;

		MasterDataBase bankKeyId = management.getMasterData(bankKey,
				MasterDataType.BANK_KEY);
		if (bankKeyId == null) {
			throw new MasterDataIdentityNotDefined(bankKey,
					MasterDataType.BANK_KEY);
		}
		_bankKey = bankKeyId.getIdentity();
	}

	/**
	 * set bank account name
	 * 
	 * @param accNum
	 *            bank account number
	 * @throws NullValueNotAcceptable
	 */
	public void setBankAccountNumber(BankAccountNumber accNum)
			throws NullValueNotAcceptable {
		if (accNum == null) {
			throw new NullValueNotAcceptable("Bank Account Number");
		}

		this.setDirtyData();
		_accNumber = accNum;
	}

	/**
	 * get bank account number
	 * 
	 * @return bank account number
	 */
	public BankAccountNumber getBankAccountNumber() {
		return _accNumber;
	}

	/**
	 * set the identity of bank key
	 * 
	 * @param setBankKey
	 *            identity of bank key
	 * @return boolean. If bank key identity is defined in the application,
	 *         return true. Else return false.
	 * @throws MasterDataIdentityNotDefined
	 * @throws NullValueNotAcceptable
	 */
	public void setBankKey(MasterDataIdentity bankKey)
			throws MasterDataIdentityNotDefined, NullValueNotAcceptable {
		if (bankKey == null) {
			throw new NullValueNotAcceptable("Bank Key");
		}

		MasterDataManagement management = this._management;
		MasterDataBase bankKeyId = management.getMasterData(bankKey,
				MasterDataType.BANK_KEY);
		if (bankKeyId == null) {
			throw new MasterDataIdentityNotDefined(bankKey,
					MasterDataType.BANK_KEY);
		}

		this.setDirtyData();
		_bankKey = bankKeyId.getIdentity();
	}

	/**
	 * get bank key
	 * 
	 * @return identity of bank key
	 */
	public MasterDataIdentity getBankKey() {
		return _bankKey;
	}

	/**
	 * set bank account type
	 * 
	 * @param bankAccType
	 *            bank account type
	 * @throws NullValueNotAcceptable
	 */
	public void setBankAccType(BankAccountType bankAccType)
			throws NullValueNotAcceptable {
		if (bankAccType == null) {
			throw new NullValueNotAcceptable("Bank Account Type");
		}
		this.setDirtyData();
		_bankAccType = bankAccType;
	}

	/**
	 * get bank account type
	 * 
	 * @return bank account type
	 */
	public BankAccountType getBankAccType() {
		return _bankAccType;
	}

	@Override
	public String toXML() {
		String superStr = super.toXML();

		StringBuilder strBuilder = new StringBuilder(superStr);
		strBuilder.append(String.format("%s=\"%s\" %s=\"%s\" %s=\"%s\"",
				MasterDataUtils.XML_BANK_ACCOUNT, _accNumber.toString(),
				MasterDataUtils.XML_BANK_KEY, _bankKey.toString(),
				MasterDataUtils.XML_TYPE, _bankAccType.toString()));
		return strBuilder.toString();
	}
}

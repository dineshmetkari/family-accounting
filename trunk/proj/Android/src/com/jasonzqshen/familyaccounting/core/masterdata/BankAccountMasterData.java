package com.jasonzqshen.familyaccounting.core.masterdata;

import org.w3c.dom.Element;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.utils.BankAccountType;

public class BankAccountMasterData extends MasterDataBase {
	public static final int BANK_NUM_LENGTH = 10;

	/**
	 * parser
	 */
	public static IMasterDataParser PARSER = new IMasterDataParser() {
		public MasterDataBase parse(CoreDriver coreDriver, Element elem)
				throws Exception {
			String id = elem.getAttribute(MasterDataUtils.XML_ID);
			String descp = elem.getAttribute(MasterDataUtils.XML_DESCP);
			String bankKey = elem.getAttribute(MasterDataUtils.XML_BANK_KEY);
			String bankAcc = elem
					.getAttribute(MasterDataUtils.XML_BANK_ACCOUNT);
			String type = elem.getAttribute(MasterDataUtils.XML_TYPE);

			MasterDataIdentity identity = new MasterDataIdentity(
					id.toCharArray());
			BankAccountMasterData bankAccount = new BankAccountMasterData(
					coreDriver, identity, descp);

			// bank key
			if (bankKey != null) {
				MasterDataIdentity bankKeyId = new MasterDataIdentity(
						bankKey.toCharArray());
				boolean ret = bankAccount.setBankKey(bankKeyId);
				if (ret == false) {
					throw new MasterDataIdentityNotDefined(bankKeyId,
							MasterDataType.BANK_KEY);
				}
			}
			// bank account
			if (bankAcc != null) {
				bankAccount._accNumber = new BankAccountNumber(
						bankAcc.toCharArray());
			}
			// bank type
			if (type != null) {
				bankAccount._bankAccType = Enum.valueOf(BankAccountType.class,
						type);
			}

			return bankAccount;
		}
	};

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
	 */
	public BankAccountMasterData(CoreDriver coreDriver, MasterDataIdentity id,
			String descp) {
		super(coreDriver, id, descp, PARSER);
	}

	/**
	 * set bank account name
	 * 
	 * @param accNum
	 *            bank account number
	 */
	public void setBankAccountNumber(BankAccountNumber accNum) {
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
	 */
	public boolean setBankKey(MasterDataIdentity bankKey) {
		MasterDataManagement management = _coreDriver.getMasterDataManagement();
		MasterDataIdentity bankKeyId = management.getMasterData(bankKey,
				MasterDataType.BANK_KEY).getIdentity();
		if (bankKeyId == null) {
			return false;
		}
		_bankKey = bankKeyId;
		return true;
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
	 */
	public void setBankAccType(BankAccountType bankAccType) {
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
}

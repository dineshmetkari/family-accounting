package com.jasonzqshen.familyaccounting.core.masterdata;

import org.w3c.dom.Element;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityExists;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.ParametersException;
import com.jasonzqshen.familyaccounting.core.exception.SystemException;
import com.jasonzqshen.familyaccounting.core.utils.BankAccountType;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;
import com.jasonzqshen.familyaccounting.core.utils.StringUtility;

public class BankAccountMasterDataFactory extends MasterDataFactoryBase {
	/**
	 * just construct the instance
	 * 
	 * @param coreDriver
	 */
	public BankAccountMasterDataFactory(CoreDriver coreDriver) {
		super(coreDriver);
	}

	@Override
	public MasterDataBase createNewMasterDataBase(MasterDataIdentity identity,
			String descp, Object... objects) throws ParametersException,
			MasterDataIdentityExists, MasterDataIdentityNotDefined, SystemException {
		if (objects.length != 3) {
			throw new ParametersException(String.format(
					CoreMessage.ERR_PARAMETER_LENGTH, 3, objects.length));
		}

		// check identity is duplicated
		if (_list.containsKey(identity)) {
			throw new MasterDataIdentityExists();
		}

		BankAccountNumber accNumber = null;
		MasterDataIdentity bankKey = null;
		BankAccountType type = null;
		if (!(objects[0] instanceof BankAccountNumber)) {
			throw new ParametersException(String.format(
					CoreMessage.ERR_PARAMETER_TYPE,
					BankAccountNumber.class.getName()));
		}
		accNumber = (BankAccountNumber) objects[0];

		if (!(objects[1] instanceof MasterDataIdentity)) {
			throw new ParametersException(String.format(
					CoreMessage.ERR_PARAMETER_TYPE,
					MasterDataIdentity.class.getName()));
		}
		bankKey = (MasterDataIdentity) objects[1];

		if (!(objects[2] instanceof BankAccountType)) {
			throw new ParametersException(String.format(
					CoreMessage.ERR_PARAMETER_TYPE,
					BankAccountType.class.getName()));
		}
		type = (BankAccountType) objects[2];

		BankAccountMasterData bankAccount;
		try {
			bankAccount = new BankAccountMasterData(_coreDriver, identity,
					descp, accNumber, bankKey, type);
		} catch (NullValueNotAcceptable e) {
			throw new SystemException(e);
		}

		// add to list
		this._list.put(identity, bankAccount);
		return bankAccount;
	}

	@Override
	public MasterDataBase parseMasterData(CoreDriver coreDriver, Element elem)
			throws Exception {
		String id = elem.getAttribute(MasterDataUtils.XML_ID);
		String descp = elem.getAttribute(MasterDataUtils.XML_DESCP);
		String bankKey = elem.getAttribute(MasterDataUtils.XML_BANK_KEY);
		String bankAcc = elem.getAttribute(MasterDataUtils.XML_BANK_ACCOUNT);
		String typeStr = elem.getAttribute(MasterDataUtils.XML_TYPE);
		// check attribute
		if (StringUtility.isNullOrEmpty(descp)) {
			throw new MandatoryFieldIsMissing(MasterDataUtils.XML_DESCP);
		}
		if (StringUtility.isNullOrEmpty(bankKey)) {
			throw new MandatoryFieldIsMissing(MasterDataUtils.XML_BANK_KEY);
		}
		if (StringUtility.isNullOrEmpty(bankAcc)) {
			throw new MandatoryFieldIsMissing(MasterDataUtils.XML_BANK_ACCOUNT);
		}
		if (StringUtility.isNullOrEmpty(typeStr)) {
			throw new MandatoryFieldIsMissing(MasterDataUtils.XML_TYPE);
		}

		MasterDataIdentity identity = new MasterDataIdentity(id.toCharArray());

		// bank key
		MasterDataIdentity bankKeyId = new MasterDataIdentity(
				bankKey.toCharArray());
		// bank account
		BankAccountNumber accNum = new BankAccountNumber(bankAcc.toCharArray());
		// bank type
		BankAccountType type = BankAccountType.parse(typeStr.charAt(0));

		BankAccountMasterData bankAccount = (BankAccountMasterData) this
				.createNewMasterDataBase(identity, descp, accNum, bankKeyId,
						type);
		return bankAccount;
	}
}

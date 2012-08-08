package com.jasonzqshen.familyaccounting.core.masterdata;

import org.w3c.dom.Element;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataFileFormatException;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityExists;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.ParametersException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.utils.BankAccountType;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;
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
			MasterDataIdentityExists, MasterDataIdentityNotDefined {
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

		this._containDirtyData = true;

		_coreDriver
				.logDebugInfo(
						this.getClass(),
						84,
						String.format("Create bank account (%s).",
								bankAccount.toXML()), MessageType.INFO);
		return bankAccount;
	}

	@Override
	public MasterDataBase parseMasterData(CoreDriver coreDriver, Element elem)
			throws MasterDataFileFormatException {
		String id = elem.getAttribute(MasterDataUtils.XML_ID);
		String descp = elem.getAttribute(MasterDataUtils.XML_DESCP);
		String bankKey = elem.getAttribute(MasterDataUtils.XML_BANK_KEY);
		String bankAcc = elem.getAttribute(MasterDataUtils.XML_BANK_ACCOUNT);
		String typeStr = elem.getAttribute(MasterDataUtils.XML_TYPE);

		// check attribute
		if (StringUtility.isNullOrEmpty(bankKey)) {
			_coreDriver.logDebugInfo(this.getClass(), 93, String.format(
					"Mandatory Field %s with no value",
					MasterDataUtils.XML_BANK_KEY), MessageType.ERROR);
			throw new MasterDataFileFormatException(MasterDataType.BANK_ACCOUNT);
		}

		if (StringUtility.isNullOrEmpty(bankAcc)) {
			_coreDriver.logDebugInfo(this.getClass(), 100, String.format(
					"Mandatory Field %s with no value",
					MasterDataUtils.XML_BANK_ACCOUNT), MessageType.ERROR);
			throw new MasterDataFileFormatException(MasterDataType.BANK_ACCOUNT);
		}

		if (StringUtility.isNullOrEmpty(typeStr)) {
			_coreDriver.logDebugInfo(this.getClass(), 114, String.format(
					"Mandatory Field %s with no value",
					MasterDataUtils.XML_TYPE), MessageType.ERROR);
			throw new MasterDataFileFormatException(MasterDataType.BANK_ACCOUNT);
		}

		MasterDataIdentity identity;
		try {
			identity = new MasterDataIdentity(id.toCharArray());
			// bank key
			MasterDataIdentity bankKeyId = new MasterDataIdentity(
					bankKey.toCharArray());
			// bank account
			BankAccountNumber accNum = new BankAccountNumber(
					bankAcc.toCharArray());
			// bank type
			BankAccountType type = BankAccountType.parse(typeStr.charAt(0));

			BankAccountMasterData bankAccount = (BankAccountMasterData) this
					.createNewMasterDataBase(identity, descp, accNum,
							bankKeyId, type);

			_coreDriver.logDebugInfo(
					this.getClass(),
					130,
					String.format("Parse bank account (%s).",
							bankAccount.toXML()), MessageType.INFO);
			return bankAccount;
		} catch (IdentityTooLong e) {
			_coreDriver.logDebugInfo(this.getClass(), 150,
					"Master data identity is too long.", MessageType.ERROR);
			throw new MasterDataFileFormatException(MasterDataType.BANK_ACCOUNT);
		} catch (IdentityNoData e) {
			_coreDriver
					.logDebugInfo(this.getClass(), 154,
							"Master data identity is with no value.",
							MessageType.ERROR);
			throw new MasterDataFileFormatException(MasterDataType.BANK_ACCOUNT);
		} catch (IdentityInvalidChar e) {
			_coreDriver.logDebugInfo(this.getClass(), 160,
					"Invalid character in identity.", MessageType.ERROR);
			throw new MasterDataFileFormatException(MasterDataType.BANK_ACCOUNT);
		} catch (ParametersException e) {
			_coreDriver.logDebugInfo(this.getClass(), 164,
					"Function parameter set error: " + e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		} catch (MasterDataIdentityExists e) {
			_coreDriver.logDebugInfo(this.getClass(), 168,
					"Master data identity duplicated.", MessageType.ERROR);
			throw new MasterDataFileFormatException(MasterDataType.BANK_ACCOUNT);
		} catch (MasterDataIdentityNotDefined e) {
			_coreDriver.logDebugInfo(this.getClass(), 173,
					"Identity has not been defined.", MessageType.ERROR);
			throw new MasterDataFileFormatException(MasterDataType.BANK_ACCOUNT);
		}

	}
}

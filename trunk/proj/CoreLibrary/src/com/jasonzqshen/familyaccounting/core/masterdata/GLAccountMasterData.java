package com.jasonzqshen.familyaccounting.core.masterdata;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NoGLAccountGroupException;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.format.GLAccountGroupFormatException;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

public class GLAccountMasterData extends MasterDataBase {
	public static final String FILE_NAME = "gl_account.xml";
	private MasterDataIdentity _bankAccount;
	private final GLAccountGroup _group;

	/**
	 * 
	 * @param id
	 * @param descp
	 * @param parser
	 * @throws NullValueNotAcceptable
	 * @throws MasterDataIdentityNotDefined
	 * @throws NoGLAccountGroupException
	 */
	public GLAccountMasterData(CoreDriver coreDriver,
			MasterDataManagement management, MasterDataIdentity_GLAccount id,
			String descp) throws NullValueNotAcceptable,
			MasterDataIdentityNotDefined, NoGLAccountGroupException {
		this(coreDriver, management, id, descp, null);
	}

	/**
	 * 
	 * @param coreDriver
	 * @param id
	 * @param descp
	 * @param type
	 * @param group
	 * @throws NullValueNotAcceptable
	 * @throws MasterDataIdentityNotDefined
	 * @throws NoGLAccountGroupException
	 */
	public GLAccountMasterData(CoreDriver coreDriver,
			MasterDataManagement management, MasterDataIdentity_GLAccount id,
			String descp, MasterDataIdentity bankAccount)
			throws NullValueNotAcceptable, MasterDataIdentityNotDefined,
			NoGLAccountGroupException {
		super(coreDriver, management, id, descp);
		// check id and get group
		String groupId = id.toString().substring(0, 4);
		try {
			_group = GLAccountGroup.parse(groupId);
		} catch (GLAccountGroupFormatException e) {
			throw new NoGLAccountGroupException(groupId);
		}

		if (bankAccount == null) {
			_bankAccount = null;
		} else {
			MasterDataBase bankAccountId = _management.getMasterData(
					bankAccount, MasterDataType.BANK_ACCOUNT);
			if (bankAccountId == null) {
				throw new MasterDataIdentityNotDefined(bankAccount,
						MasterDataType.BANK_ACCOUNT);
			}
			_bankAccount = bankAccountId.getIdentity();
		}

	}

	/**
	 * get identity for G/L account
	 * 
	 * @return identity
	 */
	public MasterDataIdentity_GLAccount getGLIdentity() {
		return (MasterDataIdentity_GLAccount) _identity;
	}

	/**
	 * get G/L account group
	 * 
	 * @return
	 */
	public GLAccountGroup getGroup() {
		return _group;
	}

	/**
	 * Set bank account
	 * 
	 * @param bankAccount
	 * @throws MasterDataIdentityNotDefined
	 */
	public void setBankAccount(MasterDataIdentity bankAccount)
			throws MasterDataIdentityNotDefined {
		if (bankAccount == null) {
			// clear
			_bankAccount = null;
			return;
		}
		MasterDataManagement management = _coreDriver.getMasterDataManagement();
		MasterDataBase bankAccountId = management.getMasterData(bankAccount,
				MasterDataType.BANK_ACCOUNT);
		if (bankAccountId == null) {
			throw new MasterDataIdentityNotDefined(bankAccount,
					MasterDataType.BANK_ACCOUNT);
		}
		this.setDirtyData();
		_bankAccount = bankAccountId.getIdentity();
	}

	/**
	 * get bank account
	 * 
	 * @return identity of bank account
	 */
	public MasterDataIdentity getBankAccount() {
		return _bankAccount;
	}

	@Override
	public String toXML() {
		String superStr = super.toXML();

		StringBuilder strBuilder = new StringBuilder(superStr);

		if (_bankAccount != null) {
			strBuilder.append(String.format("%s=\"%s\" ",
					MasterDataUtils.XML_BANK_ACCOUNT, _bankAccount.toString()));
		}
		return strBuilder.toString();
	}
}

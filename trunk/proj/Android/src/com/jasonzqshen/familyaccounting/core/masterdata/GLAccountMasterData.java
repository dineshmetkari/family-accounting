package com.jasonzqshen.familyaccounting.core.masterdata;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountType;

public class GLAccountMasterData extends MasterDataBase {
	public static final String FILE_NAME = "gl_account.xml";

	private GLAccountType _type;
	private MasterDataIdentity _bankAccount;
	private MasterDataIdentity _group;

	/**
	 * 
	 * @param id
	 * @param descp
	 * @param parser
	 * @throws NullValueNotAcceptable
	 * @throws MasterDataIdentityNotDefined
	 */
	public GLAccountMasterData(CoreDriver coreDriver,
			MasterDataIdentity_GLAccount id, String descp, GLAccountType type,
			MasterDataIdentity group) throws NullValueNotAcceptable,
			MasterDataIdentityNotDefined {
		this(coreDriver, id, descp, type, group, null);
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
	 */
	public GLAccountMasterData(CoreDriver coreDriver,
			MasterDataIdentity_GLAccount id, String descp, GLAccountType type,
			MasterDataIdentity group, MasterDataIdentity bankAccount)
			throws NullValueNotAcceptable, MasterDataIdentityNotDefined {
		super(coreDriver, id, descp);

		this.setBankAccount(bankAccount);
		this.setGLAccountType(type);
		this.setAccountGroup(group);
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
	 * set G/L account type
	 * 
	 * @param type
	 * @throws NullValueNotAcceptable
	 */
	public void setGLAccountType(GLAccountType type)
			throws NullValueNotAcceptable {
		if (type == null) {
			throw new NullValueNotAcceptable("GL account type");
		}
		_type = type;
	}

	/**
	 * get G/L account type
	 * 
	 * @return
	 */
	public GLAccountType getGLAccountType() {
		return _type;
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
		MasterDataIdentity bankAccountId = management.getMasterData(
				bankAccount, MasterDataType.BANK_ACCOUNT).getIdentity();
		if (bankAccountId == null) {
			throw new MasterDataIdentityNotDefined(bankAccount,
					MasterDataType.BANK_ACCOUNT);
		}

		_bankAccount = bankAccountId;
	}

	/**
	 * get bank account
	 * 
	 * @return identity of bank account
	 */
	public MasterDataIdentity getBankAccount() {
		return _bankAccount;
	}

	/**
	 * set G/L account group
	 * 
	 * @param group
	 *            id of G/L account group
	 * @return result of the action
	 * @throws MasterDataIdentityNotDefined
	 * @throws NullValueNotAcceptable
	 */
	public boolean setAccountGroup(MasterDataIdentity group)
			throws MasterDataIdentityNotDefined, NullValueNotAcceptable {
		if (group == null) {
			throw new NullValueNotAcceptable("G/L account group");
		}
		MasterDataManagement management = _coreDriver.getMasterDataManagement();
		MasterDataIdentity groupId = management.getMasterData(group,
				MasterDataType.GL_ACCOUNT_GROUP).getIdentity();
		if (groupId == null) {
			throw new MasterDataIdentityNotDefined(group,
					MasterDataType.GL_ACCOUNT_GROUP);
		}
		_group = groupId;
		return true;
	}

	/**
	 * get G/L account group
	 * 
	 * @return id of G/L account group
	 */
	public MasterDataIdentity getAccountGroup() {
		return _group;
	}

	@Override
	public String toXML() {
		String superStr = super.toXML();

		StringBuilder strBuilder = new StringBuilder(superStr);
		strBuilder.append(String.format("%s=\"%s\" %s=\"%s\" ",
				MasterDataUtils.XML_TYPE, _type.toString(),
				MasterDataUtils.XML_GROUP, _group.toString()));

		if (_bankAccount != null) {
			strBuilder.append(String.format("%s=\"%s\" ",
					MasterDataUtils.XML_BANK_ACCOUNT, _bankAccount.toString()));
		}
		return strBuilder.toString();
	}
}

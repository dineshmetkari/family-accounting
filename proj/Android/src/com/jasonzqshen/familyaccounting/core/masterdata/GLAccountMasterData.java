package com.jasonzqshen.familyaccounting.core.masterdata;

import org.w3c.dom.Element;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountType;

public class GLAccountMasterData extends MasterDataBase {
	/**
	 * parser
	 */
	public static IMasterDataParser PARSER = new IMasterDataParser() {
		public MasterDataBase parse(CoreDriver coreDriver, Element elem)
				throws Exception {
			String id = elem.getAttribute(MasterDataUtils.XML_ID);
			String descp = elem.getAttribute(MasterDataUtils.XML_DESCP);
			String typeStr = elem.getAttribute(MasterDataUtils.XML_TYPE);
			String bankAccoutStr = elem.getAttribute(MasterDataUtils.XML_BANK_ACCOUNT);
			String groupStr = elem.getAttribute(MasterDataUtils.XML_GROUP);

			MasterDataIdentity_GLAccount identity = new MasterDataIdentity_GLAccount(
					id.toCharArray());
			GLAccountMasterData glAccount = new GLAccountMasterData(coreDriver,
					identity, descp);
			return glAccount;
		}
	};

	private GLAccountType _type;
	private MasterDataIdentity _bankAccount;
	private MasterDataIdentity _group;

	/**
	 * 
	 * @param id
	 * @param descp
	 * @param parser
	 */
	public GLAccountMasterData(CoreDriver coreDriver,
			MasterDataIdentity_GLAccount id, String descp) {
		super(coreDriver, id, descp, PARSER);
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
	 */
	public void setGLAccountType(GLAccountType type) {
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
	 */
	public boolean setBankAccount(MasterDataIdentity bankAccount) {
		MasterDataManagement management = _coreDriver.getMasterDataManagement();
		MasterDataIdentity bankAccountId = management.getMasterData(
				bankAccount, MasterDataType.BANK_ACCOUNT).getIdentity();
		if (bankAccountId == null) {
			return false;
		}

		_bankAccount = bankAccountId;
		return true;
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
	 */
	public boolean setAccountGroup(MasterDataIdentity group) {
		MasterDataManagement management = _coreDriver.getMasterDataManagement();
		MasterDataIdentity groupId = management.getMasterData(group,
				MasterDataType.GL_ACCOUNT_GROUP).getIdentity();
		if (groupId == null) {
			return false;
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
}

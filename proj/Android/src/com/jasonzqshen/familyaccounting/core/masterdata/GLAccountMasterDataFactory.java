package com.jasonzqshen.familyaccounting.core.masterdata;

import org.w3c.dom.Element;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityExists;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.ParametersException;
import com.jasonzqshen.familyaccounting.core.exception.SystemException;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountType;
import com.jasonzqshen.familyaccounting.core.utils.StringUtility;

public class GLAccountMasterDataFactory extends MasterDataFactoryBase {
	/**
	 * 
	 * @param parser
	 * @param coreDriver
	 */
	public GLAccountMasterDataFactory(CoreDriver coreDriver) {
		super(coreDriver);
	}

	@Override
	public MasterDataBase createNewMasterDataBase(MasterDataIdentity identity,
			String descp, Object... objects) throws ParametersException,
			MasterDataIdentityNotDefined, SystemException, MasterDataIdentityExists {
		// check id is G/L identity
		if (!(identity instanceof MasterDataIdentity_GLAccount)) {
			throw new ParametersException(
					"Type of master data identity should be MasterDataIdentity_GLAccount.");
		}
		// check duplicated
		if (_list.containsKey(identity)) {
			throw new MasterDataIdentityExists();
		}

		MasterDataIdentity_GLAccount identity_gl = (MasterDataIdentity_GLAccount) identity;

		GLAccountType type = null;
		MasterDataIdentity group = null;
		MasterDataIdentity bankAccount = null;

		if (objects.length == 2 || objects.length == 3) {
		} else {
			throw new ParametersException(String.format(
					CoreMessage.ERR_PARAMETER_LENGTH, 3, objects.length));
		}

		// gl account type
		if (!(objects[0] instanceof GLAccountType)) {
			throw new ParametersException(String.format(
					CoreMessage.ERR_PARAMETER_TYPE,
					GLAccountType.class.getName()));
		}
		type = (GLAccountType) objects[0];

		// group
		if (!(objects[1] instanceof MasterDataIdentity)) {
			throw new ParametersException(String.format(
					CoreMessage.ERR_PARAMETER_TYPE,
					MasterDataIdentity.class.getName()));
		}
		group = (MasterDataIdentity) objects[1];

		if (objects.length == 3) {
			// bank account
			if (!(objects[2] instanceof MasterDataIdentity)) {
				throw new ParametersException(String.format(
						CoreMessage.ERR_PARAMETER_TYPE,
						MasterDataIdentity.class.getName()));
			}
			bankAccount = (MasterDataIdentity) objects[2];
		}

		GLAccountMasterData glAccount;
		try {
			glAccount = new GLAccountMasterData(_coreDriver, identity_gl,
					descp, type, group, bankAccount);
		} catch (NullValueNotAcceptable e) {
			throw new SystemException(e);
		}

		this._list.put(identity_gl, glAccount);
		return glAccount;
	}

	@Override
	public MasterDataBase parseMasterData(CoreDriver coreDriver, Element elem)
			throws Exception {
		String id = elem.getAttribute(MasterDataUtils.XML_ID);
		String descp = elem.getAttribute(MasterDataUtils.XML_DESCP);
		String typeStr = elem.getAttribute(MasterDataUtils.XML_TYPE);
		String bankAccStr = elem.getAttribute(MasterDataUtils.XML_BANK_ACCOUNT);
		String groupStr = elem.getAttribute(MasterDataUtils.XML_GROUP);
		// check attribute
		if (StringUtility.isNullOrEmpty(descp)) {
			throw new MandatoryFieldIsMissing(MasterDataUtils.XML_DESCP);
		}
		if (StringUtility.isNullOrEmpty(typeStr)) {
			throw new MandatoryFieldIsMissing(MasterDataUtils.XML_TYPE);
		}
		if (StringUtility.isNullOrEmpty(groupStr)) {
			throw new MandatoryFieldIsMissing(MasterDataUtils.XML_GROUP);
		}

		MasterDataIdentity_GLAccount identity = new MasterDataIdentity_GLAccount(
				id.toCharArray());

		// G/L account type
		GLAccountType type = GLAccountType.parse(typeStr.charAt(0));

		// G/L account group
		MasterDataIdentity groupId = new MasterDataIdentity(
				groupStr.toCharArray());

		GLAccountMasterData glAccount = (GLAccountMasterData) this
				.createNewMasterDataBase(identity, descp, type, groupId);

		// bank account
		if (!StringUtility.isNullOrEmpty(bankAccStr)) {
			MasterDataIdentity bankAccId = new MasterDataIdentity(
					bankAccStr.toCharArray());
			glAccount.setBankAccount(bankAccId);
		}

		return glAccount;
	}

}

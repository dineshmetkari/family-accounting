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
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountType;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;
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
			MasterDataIdentityNotDefined, MasterDataIdentityExists {
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

		this._containDirtyData = true;
		this._list.put(identity_gl, glAccount);

		_coreDriver.logDebugInfo(this.getClass(), 84,
				String.format("Create G/L account (%s).", glAccount.toXML()),
				MessageType.INFO);
		return glAccount;
	}

	@Override
	public MasterDataBase parseMasterData(CoreDriver coreDriver, Element elem)
			throws MasterDataFileFormatException {
		String id = elem.getAttribute(MasterDataUtils.XML_ID);
		String descp = elem.getAttribute(MasterDataUtils.XML_DESCP);
		String typeStr = elem.getAttribute(MasterDataUtils.XML_TYPE);
		String groupStr = elem.getAttribute(MasterDataUtils.XML_GROUP);
		// check attribute
		if (StringUtility.isNullOrEmpty(typeStr)) {
			_coreDriver.logDebugInfo(this.getClass(), 111064, String.format(
					"Mandatory Field %s with no value",
					MasterDataUtils.XML_TYPE), MessageType.ERROR);
			throw new MasterDataFileFormatException(MasterDataType.GL_ACCOUNT);
		}

		String bankAccStr = elem.getAttribute(MasterDataUtils.XML_BANK_ACCOUNT);
		if (StringUtility.isNullOrEmpty(groupStr)) {
			_coreDriver.logDebugInfo(this.getClass(), 111064, String.format(
					"Mandatory Field %s with no value",
					MasterDataUtils.XML_GROUP), MessageType.ERROR);
			throw new MasterDataFileFormatException(MasterDataType.GL_ACCOUNT);
		}

		try {
			MasterDataIdentity_GLAccount identity = new MasterDataIdentity_GLAccount(
					id.toCharArray());

			// G/L account type
			GLAccountType type = GLAccountType.parse(typeStr.charAt(0));

			// G/L account group
			MasterDataIdentity groupId = new MasterDataIdentity(
					groupStr.toCharArray());

			GLAccountMasterData glAccount = null;

			// bank account
			if (StringUtility.isNullOrEmpty(bankAccStr)) {
				glAccount = (GLAccountMasterData) this.createNewMasterDataBase(
						identity, descp, type, groupId);
			} else {
				MasterDataIdentity bankAccId = new MasterDataIdentity(
						bankAccStr.toCharArray());
				glAccount = (GLAccountMasterData) this.createNewMasterDataBase(
						identity, descp, type, groupId, bankAccId);

			}

			_coreDriver
					.logDebugInfo(
							this.getClass(),
							167,
							String.format("Parse G/L account (%s).",
									glAccount.toXML()), MessageType.INFO);
			return glAccount;
		} catch (IdentityTooLong e) {
			_coreDriver.logDebugInfo(this.getClass(), 150,
					"Master data identity is too long.", MessageType.ERROR);
			throw new MasterDataFileFormatException(MasterDataType.GL_ACCOUNT);
		} catch (IdentityNoData e) {
			_coreDriver
					.logDebugInfo(this.getClass(), 154,
							"Master data identity is with no value.",
							MessageType.ERROR);
			throw new MasterDataFileFormatException(MasterDataType.GL_ACCOUNT);
		} catch (IdentityInvalidChar e) {
			_coreDriver.logDebugInfo(this.getClass(), 160,
					"Invalid character in identity.", MessageType.ERROR);
			throw new MasterDataFileFormatException(MasterDataType.GL_ACCOUNT);
		} catch (ParametersException e) {
			_coreDriver.logDebugInfo(this.getClass(), 164,
					"Function parameter set error: " + e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		} catch (MasterDataIdentityExists e) {
			_coreDriver.logDebugInfo(this.getClass(), 168,
					"Master data identity duplicated.", MessageType.ERROR);
			throw new MasterDataFileFormatException(MasterDataType.GL_ACCOUNT);
		} catch (MasterDataIdentityNotDefined e) {
			_coreDriver.logDebugInfo(this.getClass(), 173,
					"Identity has not been defined.", MessageType.ERROR);
			throw new MasterDataFileFormatException(MasterDataType.GL_ACCOUNT);
		}
	}

}

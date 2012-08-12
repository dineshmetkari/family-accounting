package com.jasonzqshen.familyaccounting.core.masterdata;

import java.util.ArrayList;

import org.w3c.dom.Element;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityExists;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NoGLAccountGroupException;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.ParametersException;
import com.jasonzqshen.familyaccounting.core.exception.format.MasterDataFileFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;
import com.jasonzqshen.familyaccounting.core.utils.StringUtility;

public class GLAccountMasterDataFactory extends MasterDataFactoryBase {
	/**
	 * 
	 * @param parser
	 * @param coreDriver
	 */
	public GLAccountMasterDataFactory(CoreDriver coreDriver, MasterDataManagement management) {
		super(coreDriver, management);
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

		MasterDataIdentity bankAccount = null;

		if (objects.length == 0 || objects.length == 1) {
		} else {
			throw new ParametersException(String.format(
					CoreMessage.ERR_PARAMETER_LENGTH, 1, objects.length));
		}

		if (objects.length == 1) {
			// bank account
			if (!(objects[0] instanceof MasterDataIdentity)) {
				throw new ParametersException(String.format(
						CoreMessage.ERR_PARAMETER_TYPE,
						MasterDataIdentity.class.getName()));
			}
			bankAccount = (MasterDataIdentity) objects[0];
		}

		GLAccountMasterData glAccount;
		try {
			glAccount = new GLAccountMasterData(_coreDriver, _management, identity_gl,
					descp, bankAccount);
		} catch (NullValueNotAcceptable e) {
			throw new SystemException(e);
		} catch (NoGLAccountGroupException e) {
			throw new ParametersException(identity_gl.toString());
		}

		this._containDirtyData = true;
		this._list.put(identity_gl, glAccount);

		// raise create master data
		_coreDriver.getListenersManagement().createMasterData(this, glAccount);
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

		String bankAccStr = elem.getAttribute(MasterDataUtils.XML_BANK_ACCOUNT);

		try {
			MasterDataIdentity_GLAccount identity = new MasterDataIdentity_GLAccount(
					id.toCharArray());

			GLAccountMasterData glAccount = null;

			// bank account
			if (StringUtility.isNullOrEmpty(bankAccStr)) {
				glAccount = (GLAccountMasterData) this.createNewMasterDataBase(
						identity, descp);
			} else {
				MasterDataIdentity bankAccId = new MasterDataIdentity(
						bankAccStr.toCharArray());
				glAccount = (GLAccountMasterData) this.createNewMasterDataBase(
						identity, descp, bankAccId);

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

	/**
	 * get balance accounts
	 * 
	 * @return
	 */
	public GLAccountMasterData[] getBalanceAccounts() {
		return getAccounts(GLAccountGroup.BALANCE_GROUP);
	}

	/**
	 * get liquidity accounts
	 * 
	 * @return
	 */
	public GLAccountMasterData[] getLiquidityAccounts() {
		return getAccounts(GLAccountGroup.Liquidity_GROUP);
	}

	/**
	 * get liability account
	 * 
	 * @return
	 */
	public GLAccountMasterData[] getLiabilityAccounts() {
		return getAccounts(GLAccountGroup.LIABILITIES_GROUP);
	}

	/**
	 * get revenue account
	 * 
	 * @return
	 */
	public GLAccountMasterData[] getRevenueAccounts() {
		return getAccounts(GLAccountGroup.REVENUE_GROUP);
	}

	/**
	 * get cost account
	 * 
	 * @return
	 */
	public GLAccountMasterData[] getCostAccounts() {
		return getAccounts(GLAccountGroup.COST_GROUP);
	}

	/**
	 * get G/L accounts base on the group
	 * 
	 * @param groups
	 * @return
	 */
	private GLAccountMasterData[] getAccounts(GLAccountGroup[] groups) {
		ArrayList<GLAccountMasterData> array = new ArrayList<GLAccountMasterData>();
		for (MasterDataBase master : this._list.values()) {
			GLAccountMasterData glAccount = (GLAccountMasterData) master;
			for (int i = 0; i < groups.length; ++i) {
				if (glAccount.getGroup() == groups[i]) {
					array.add(glAccount);
					break;
				}
			}
		}

		GLAccountMasterData[] ret = new GLAccountMasterData[array.size()];
		for (int i = 0; i < array.size(); ++i) {
			ret[i] = array.get(i);
		}
		return ret;
	}
}

package com.jasonzqshen.familyaccounting.core.masterdata;

import java.io.IOException;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;

/**
 * Base class of master data
 * 
 * @author I072485
 * 
 */
public abstract class MasterDataBase {

	protected final MasterDataIdentity _identity;
	protected String _descp; // description
	protected final CoreDriver _coreDriver; // core driver

	protected MasterDataBase(CoreDriver coreDriver, MasterDataIdentity id,
			String descp) throws NullValueNotAcceptable {
		if (id == null) {
			throw new NullValueNotAcceptable("Identity");
		}
		_coreDriver = coreDriver;
		_identity = id;

		_descp = descp;

	}

	/**
	 * get master data identity
	 * 
	 * @return
	 */
	public MasterDataIdentity getIdentity() {
		return _identity;
	}

	/**
	 * set description
	 * 
	 * @param descp
	 * @throws NullValueNotAcceptable
	 */
	public void setDescp(String descp) throws NullValueNotAcceptable {
		if (descp == null) {
			throw new NullValueNotAcceptable("Desciption");
		}

		setDirtyData();
		_descp = descp;
	}

	/**
	 * set dirty data
	 */
	protected void setDirtyData() {
		MasterDataFactoryBase factory = null;
		if (this instanceof BankAccountMasterData) {
			factory = _coreDriver.getMasterDataManagement()
					.getMasterDataFactory(MasterDataType.BANK_ACCOUNT);
		} else if (this instanceof BankKeyMasterData) {
			factory = _coreDriver.getMasterDataManagement()
					.getMasterDataFactory(MasterDataType.BANK_KEY);
		} else if (this instanceof BusinessAreaMasterData) {
			factory = _coreDriver.getMasterDataManagement()
					.getMasterDataFactory(MasterDataType.BUSINESS_AREA);
		} else if (this instanceof CustomerMasterData) {
			factory = _coreDriver.getMasterDataManagement()
					.getMasterDataFactory(MasterDataType.CUSTOMER);
		} else if (this instanceof GLAccountMasterData) {
			factory = _coreDriver.getMasterDataManagement()
					.getMasterDataFactory(MasterDataType.GL_ACCOUNT);
		} else if (this instanceof VendorMasterData) {
			factory = _coreDriver.getMasterDataManagement()
					.getMasterDataFactory(MasterDataType.VENDOR);
		}

		factory._containDirtyData = true;
	}

	/**
	 * get description
	 * 
	 * @return
	 */
	public String getDescp() {

		return _descp;
	}

	/**
	 * to element
	 * 
	 * @param parent
	 * @return child
	 * @throws IOException
	 * @throws IllegalStateException
	 * @throws IllegalArgumentException
	 */
	public String toXML() {
		return String.format("%s=\"%s\" %s=\"%s\" ", MasterDataUtils.XML_ID,
				_identity.toString(), MasterDataUtils.XML_DESCP, _descp);
	}
}

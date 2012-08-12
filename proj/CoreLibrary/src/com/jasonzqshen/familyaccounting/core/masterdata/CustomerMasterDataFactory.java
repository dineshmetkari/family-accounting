package com.jasonzqshen.familyaccounting.core.masterdata;

import org.w3c.dom.Element;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityExists;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.ParametersException;
import com.jasonzqshen.familyaccounting.core.exception.format.MasterDataFileFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;

public class CustomerMasterDataFactory extends MasterDataFactoryBase {

	public CustomerMasterDataFactory(CoreDriver coreDriver) {
		super(coreDriver);
	}

	@Override
	public MasterDataBase createNewMasterDataBase(MasterDataIdentity id,
			String descp, Object... objects) throws ParametersException,
			MasterDataIdentityExists {
		// check parameters
		if (objects.length != 0) {
			throw new ParametersException(String.format(
					CoreMessage.ERR_PARAMETER_LENGTH, 0, objects.length));
		}

		// check identity is duplicated
		if (_list.containsKey(id)) {
			throw new MasterDataIdentityExists();
		}

		CustomerMasterData customer;
		try {
			customer = new CustomerMasterData(_coreDriver, id, descp);
		} catch (NullValueNotAcceptable e) {
			throw new SystemException(e);
		}

		this._containDirtyData = true;
		this._list.put(id, customer);

		// raise create master data
		_coreDriver.getListenersManagement().createMasterData(this, customer);
		_coreDriver.logDebugInfo(this.getClass(), 84,
				String.format("Create customer (%s).", customer.toXML()),
				MessageType.INFO);
		return customer;
	}

	@Override
	public MasterDataBase parseMasterData(CoreDriver coreDriver, Element elem)
			throws MasterDataFileFormatException {
		String id = elem.getAttribute(MasterDataUtils.XML_ID);
		String descp = elem.getAttribute(MasterDataUtils.XML_DESCP);

		try {
			MasterDataIdentity identity = new MasterDataIdentity(
					id.toCharArray());

			CustomerMasterData customer = (CustomerMasterData) this
					.createNewMasterDataBase(identity, descp);

			_coreDriver.logDebugInfo(this.getClass(), 130,
					String.format("Parse customer (%s).", customer.toXML()),
					MessageType.INFO);
			return customer;
		} catch (IdentityTooLong e) {
			_coreDriver.logDebugInfo(this.getClass(), 150,
					"Master data identity is too long.", MessageType.ERROR);
			throw new MasterDataFileFormatException(MasterDataType.CUSTOMER);
		} catch (IdentityNoData e) {
			_coreDriver
					.logDebugInfo(this.getClass(), 154,
							"Master data identity is with no value.",
							MessageType.ERROR);
			throw new MasterDataFileFormatException(MasterDataType.CUSTOMER);
		} catch (IdentityInvalidChar e) {
			_coreDriver.logDebugInfo(this.getClass(), 160,
					"Invalid character in identity.", MessageType.ERROR);
			throw new MasterDataFileFormatException(MasterDataType.CUSTOMER);
		} catch (ParametersException e) {
			_coreDriver.logDebugInfo(this.getClass(), 164,
					"Function parameter set error: " + e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		} catch (MasterDataIdentityExists e) {
			_coreDriver.logDebugInfo(this.getClass(), 168,
					"Master data identity duplicated.", MessageType.ERROR);
			throw new MasterDataFileFormatException(MasterDataType.CUSTOMER);
		}

	}
}

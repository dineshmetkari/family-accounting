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
import com.jasonzqshen.familyaccounting.core.utils.CriticalLevel;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;
import com.jasonzqshen.familyaccounting.core.utils.StringUtility;

public class BusinessAreaMasterDataFactory extends MasterDataFactoryBase {

	public BusinessAreaMasterDataFactory(CoreDriver coreDriver,
			MasterDataManagement management) {
		super(coreDriver, management);
	}

	@Override
	public MasterDataBase createNewMasterDataBase(MasterDataIdentity identity,
			String descp, Object... objects) throws SystemException,
			ParametersException, MasterDataIdentityExists {
		if (objects.length != 1) {
			throw new ParametersException(String.format(
					CoreMessage.ERR_PARAMETER_LENGTH, 1, objects.length));
		}

		// check identity is duplicated
		if (_list.containsKey(identity)) {
			throw new MasterDataIdentityExists();
		}

		CriticalLevel l = null;
		if (!(objects[0] instanceof CriticalLevel)) {
			throw new ParametersException(String.format(
					CoreMessage.ERR_PARAMETER_TYPE,
					CriticalLevel.class.getName()));
		}
		l = (CriticalLevel) objects[0];
		BusinessAreaMasterData businessArea;
		try {
			businessArea = new BusinessAreaMasterData(_coreDriver, _management, identity,
					descp, l);
		} catch (NullValueNotAcceptable e) {
			throw new SystemException(e);
		}

		this._containDirtyData = true;
		this._list.put(identity, businessArea);

		// raise create master data
		_coreDriver.getListenersManagement().createMasterData(this,
				businessArea);
		_coreDriver.logDebugInfo(
				this.getClass(),
				59,
				String.format("Create business area (%s).",
						businessArea.toXML()), MessageType.INFO);
		return businessArea;
	}

	@Override
	public MasterDataBase parseMasterData(CoreDriver coreDriver, Element elem)
			throws MasterDataFileFormatException {
		String id = elem.getAttribute(MasterDataUtils.XML_ID);
		String descp = elem.getAttribute(MasterDataUtils.XML_DESCP);
		String criticalLevel = elem
				.getAttribute(MasterDataUtils.XML_CRITICAL_LEVEL);
		// check attribute
		if (StringUtility.isNullOrEmpty(criticalLevel)) {
			throw new MasterDataFileFormatException(
					MasterDataType.BUSINESS_AREA);
		}

		CriticalLevel l = CriticalLevel.parse(criticalLevel.charAt(0));
		try {
			MasterDataIdentity identity = new MasterDataIdentity(
					id.toCharArray());

			BusinessAreaMasterData businessArea = (BusinessAreaMasterData) this
					.createNewMasterDataBase(identity, descp, l);

			_coreDriver.logDebugInfo(
					this.getClass(),
					130,
					String.format("Parse business area (%s).",
							businessArea.toXML()), MessageType.INFO);
			return businessArea;
		} catch (IdentityTooLong e) {
			_coreDriver.logDebugInfo(this.getClass(), 150,
					"Master data identity is too long.", MessageType.ERROR);
			throw new MasterDataFileFormatException(
					MasterDataType.BUSINESS_AREA);
		} catch (IdentityNoData e) {
			_coreDriver
					.logDebugInfo(this.getClass(), 154,
							"Master data identity is with no value.",
							MessageType.ERROR);
			throw new MasterDataFileFormatException(
					MasterDataType.BUSINESS_AREA);
		} catch (IdentityInvalidChar e) {
			_coreDriver.logDebugInfo(this.getClass(), 160,
					"Invalid character in identity.", MessageType.ERROR);
			throw new MasterDataFileFormatException(
					MasterDataType.BUSINESS_AREA);
		} catch (ParametersException e) {
			_coreDriver.logDebugInfo(this.getClass(), 164,
					"Function parameter set error: " + e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		} catch (MasterDataIdentityExists e) {
			_coreDriver.logDebugInfo(this.getClass(), 168,
					"Master data identity duplicated.", MessageType.ERROR);
			throw new MasterDataFileFormatException(
					MasterDataType.BUSINESS_AREA);
		}

	}

}

package com.jasonzqshen.familyaccounting.core.masterdata;

import org.w3c.dom.Element;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityExists;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.ParametersException;
import com.jasonzqshen.familyaccounting.core.exception.SystemException;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;
import com.jasonzqshen.familyaccounting.core.utils.CriticalLevel;
import com.jasonzqshen.familyaccounting.core.utils.StringUtility;

public class BusinessAreaMasterDataFactory extends MasterDataFactoryBase {

	public BusinessAreaMasterDataFactory(CoreDriver coreDriver) {
		super(coreDriver);
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
			businessArea = new BusinessAreaMasterData(_coreDriver, identity,
					descp, l);
		} catch (NullValueNotAcceptable e) {
			throw new SystemException(e);
		}
		
		this._containDirtyData = true;
		this._list.put(identity, businessArea);
		return businessArea;
	}

	@Override
	public MasterDataBase parseMasterData(CoreDriver coreDriver, Element elem)
			throws MandatoryFieldIsMissing, SystemException {
		String id = elem.getAttribute(MasterDataUtils.XML_ID);
		String descp = elem.getAttribute(MasterDataUtils.XML_DESCP);
		String criticalLevel = elem
				.getAttribute(MasterDataUtils.XML_CRITICAL_LEVEL);
		// check attribute
		if (StringUtility.isNullOrEmpty(descp)) {
			throw new MandatoryFieldIsMissing(MasterDataUtils.XML_DESCP);
		}
		if (StringUtility.isNullOrEmpty(criticalLevel)) {
			throw new MandatoryFieldIsMissing(
					MasterDataUtils.XML_CRITICAL_LEVEL);
		}

		CriticalLevel l = CriticalLevel.parse(criticalLevel.charAt(0));
		try {
			MasterDataIdentity identity = new MasterDataIdentity(id.toCharArray());
			
			BusinessAreaMasterData businessArea = (BusinessAreaMasterData) this
					.createNewMasterDataBase(identity, descp, l);

			return businessArea;
		} catch (IdentityTooLong e) {
			throw new SystemException(e);
		} catch (IdentityNoData e) {
			throw new SystemException(e);
		} catch (IdentityInvalidChar e) {
			throw new SystemException(e);
		} catch (SystemException e) {
			throw new SystemException(e);
		} catch (ParametersException e) {
			throw new SystemException(e);
		} catch (MasterDataIdentityExists e) {
			throw new SystemException(e);
		}
		
	}

}

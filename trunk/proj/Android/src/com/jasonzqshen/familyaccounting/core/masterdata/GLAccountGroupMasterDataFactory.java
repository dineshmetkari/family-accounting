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
import com.jasonzqshen.familyaccounting.core.utils.StringUtility;

public class GLAccountGroupMasterDataFactory extends MasterDataFactoryBase {

	public GLAccountGroupMasterDataFactory(CoreDriver coreDriver) {
		super(coreDriver);
	}

	@Override
	public MasterDataBase createNewMasterDataBase(MasterDataIdentity id,
			String descp, Object... objects) throws ParametersException,
			MasterDataIdentityExists, SystemException {
		// check parameters
		if (objects.length != 0) {
			throw new ParametersException(String.format(
					CoreMessage.ERR_PARAMETER_LENGTH, 0, objects.length));
		}

		// check identity is duplicated
		if (_list.containsKey(id)) {
			throw new MasterDataIdentityExists();
		}

		GLAccountGroupMasterData group;
		try {
			group = new GLAccountGroupMasterData(_coreDriver, id, descp);
		} catch (NullValueNotAcceptable e) {
			throw new SystemException(e);
		}
		
		this._containDirtyData = true;
		this._list.put(id, group);
		return group;
	}

	@Override
	public MasterDataBase parseMasterData(CoreDriver coreDriver, Element elem)
			throws MandatoryFieldIsMissing, SystemException {
		String id = elem.getAttribute(MasterDataUtils.XML_ID);
		String descp = elem.getAttribute(MasterDataUtils.XML_DESCP);
		// check attribute
		if (StringUtility.isNullOrEmpty(descp)) {
			throw new MandatoryFieldIsMissing(MasterDataUtils.XML_DESCP);
		}

		try {
			MasterDataIdentity identity = new MasterDataIdentity(id.toCharArray());
			GLAccountGroupMasterData group = new GLAccountGroupMasterData(
					coreDriver, identity, descp);

			this._list.put(identity, group);
			return group;
		} catch (IdentityTooLong e) {
			throw new SystemException(e);
		} catch (IdentityNoData e) {
			throw new SystemException(e);
		} catch (IdentityInvalidChar e) {
			throw new SystemException(e);
		} catch (NullValueNotAcceptable e) {
			throw new SystemException(e);
		}

	}

}
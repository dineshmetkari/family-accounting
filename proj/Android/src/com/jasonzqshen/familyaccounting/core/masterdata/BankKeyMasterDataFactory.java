package com.jasonzqshen.familyaccounting.core.masterdata;

import org.w3c.dom.Element;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityExists;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.ParametersException;
import com.jasonzqshen.familyaccounting.core.exception.SystemException;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;
import com.jasonzqshen.familyaccounting.core.utils.StringUtility;

public class BankKeyMasterDataFactory extends MasterDataFactoryBase {

	public BankKeyMasterDataFactory(CoreDriver coreDriver) {
		super(coreDriver);
	}

	@Override
	public MasterDataBase createNewMasterDataBase(MasterDataIdentity id,
			String descp, Object... objects) throws ParametersException,
			MasterDataIdentityExists, SystemException {
		if (objects.length > 0) {
			throw new ParametersException(String.format(
					CoreMessage.ERR_PARAMETER_LENGTH, 0, objects.length));
		}

		// check identity is duplicated
		if (_list.containsKey(id)) {
			throw new MasterDataIdentityExists();
		}

		BankKeyMasterData bankKey;
		try {
			bankKey = new BankKeyMasterData(_coreDriver, id, descp);
		} catch (NullValueNotAcceptable e) {
			throw new SystemException(e);
		}
		this._list.put(id, bankKey);
		return bankKey;
	}

	@Override
	public MasterDataBase parseMasterData(CoreDriver coreDriver, Element elem)
			throws Exception {
		String id = elem.getAttribute(MasterDataUtils.XML_ID);
		String descp = elem.getAttribute(MasterDataUtils.XML_DESCP);
		// check attribute
		if (StringUtility.isNullOrEmpty(descp)) {
			throw new MandatoryFieldIsMissing(MasterDataUtils.XML_DESCP);
		}

		MasterDataIdentity identity = new MasterDataIdentity(id.toCharArray());
		BankKeyMasterData bankKey = (BankKeyMasterData) this
				.createNewMasterDataBase(identity, descp);
		return bankKey;
	}

}

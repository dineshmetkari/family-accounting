package com.jasonzqshen.familyaccounting.core.masterdata;

import org.w3c.dom.Element;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataFileFormatException;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityExists;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.ParametersException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;

public class GLAccountGroupMasterDataFactory extends MasterDataFactoryBase {

	public GLAccountGroupMasterDataFactory(CoreDriver coreDriver) {
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

		GLAccountGroupMasterData group;
		try {
			group = new GLAccountGroupMasterData(_coreDriver, id, descp);
		} catch (NullValueNotAcceptable e) {
			throw new SystemException(e);
		}

		this._containDirtyData = true;
		this._list.put(id, group);

		_coreDriver.logDebugInfo(this.getClass(), 48,
				String.format("Create G/L account group (%s).", group.toXML()),
				MessageType.INFO);
		return group;
	}

	@Override
	public MasterDataBase parseMasterData(CoreDriver coreDriver, Element elem)
			throws MasterDataFileFormatException {
		String id = elem.getAttribute(MasterDataUtils.XML_ID);
		String descp = elem.getAttribute(MasterDataUtils.XML_DESCP);

		try {
			MasterDataIdentity identity = new MasterDataIdentity(
					id.toCharArray());
			GLAccountGroupMasterData group = (GLAccountGroupMasterData) this
					.createNewMasterDataBase(identity, descp);

			_coreDriver.logDebugInfo(
					this.getClass(),
					62,
					String.format("Parse G/L account group (%s).",
							group.toXML()), MessageType.INFO);
			return group;
		} catch (IdentityTooLong e) {
			_coreDriver.logDebugInfo(this.getClass(), 150,
					"Master data identity is too long.", MessageType.ERROR);
			throw new MasterDataFileFormatException(
					MasterDataType.GL_ACCOUNT_GROUP);
		} catch (IdentityNoData e) {
			_coreDriver
					.logDebugInfo(this.getClass(), 154,
							"Master data identity is with no value.",
							MessageType.ERROR);
			throw new MasterDataFileFormatException(
					MasterDataType.GL_ACCOUNT_GROUP);
		} catch (IdentityInvalidChar e) {
			_coreDriver.logDebugInfo(this.getClass(), 160,
					"Invalid character in identity.", MessageType.ERROR);
			throw new MasterDataFileFormatException(
					MasterDataType.GL_ACCOUNT_GROUP);
		} catch (ParametersException e) {
			_coreDriver.logDebugInfo(this.getClass(), 164,
					"Function parameter set error: " + e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		} catch (MasterDataIdentityExists e) {
			_coreDriver.logDebugInfo(this.getClass(), 168,
					"Master data identity duplicated.", MessageType.ERROR);
			throw new MasterDataFileFormatException(
					MasterDataType.GL_ACCOUNT_GROUP);
		}

	}

}

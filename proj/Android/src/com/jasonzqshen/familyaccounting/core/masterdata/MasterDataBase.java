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
		_identity = id;

		setDescp(descp);
		_coreDriver = coreDriver;
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
		_descp = descp;
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

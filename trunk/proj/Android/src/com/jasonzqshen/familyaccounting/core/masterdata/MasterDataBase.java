package com.jasonzqshen.familyaccounting.core.masterdata;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

/**
 * Base class of master data
 * 
 * @author I072485
 * 
 */
public abstract class MasterDataBase {
	
	
	protected final MasterDataIdentity _identity;
	protected final IMasterDataParser _parser;
	protected String _descp; // description

	protected MasterDataBase(MasterDataIdentity id, String descp,
			IMasterDataParser parser) {
		_identity = id;
		_parser = parser;
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
	 * get master data parse
	 * 
	 * @return
	 */
	public IMasterDataParser getParser() {
		return _parser;
	}

	/**
	 * set description
	 * 
	 * @param descp
	 */
	public void setDescp(String descp) {
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
	public void toXML(XmlSerializer serializer)
			throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.attribute("", MasterDataUtils.XML_ID, _identity.toString());
		serializer.attribute("", MasterDataUtils.XML_DESCP, _descp.toString());
	}
}

package com.jasonzqshen.familyaccounting.core.masterdata;

import org.w3c.dom.Element;

import com.jasonzqshen.familyaccounting.core.CoreDriver;

public class GLAccountGroupMasterData extends MasterDataBase {
	/**
	 * parser
	 */
	public static IMasterDataParser PARSER = new IMasterDataParser() {
		public MasterDataBase parse(CoreDriver coreDriver, Element elem)
				throws Exception {
			String id = elem.getAttribute(MasterDataUtils.XML_ID);
			String descp = elem.getAttribute(MasterDataUtils.XML_DESCP);

			MasterDataIdentity identity = new MasterDataIdentity(
					id.toCharArray());
			GLAccountGroupMasterData accGroup = new GLAccountGroupMasterData(
					coreDriver, identity, descp);
			return accGroup;
		}
	};

	/**
	 * 
	 * @param id
	 * @param descp
	 * @param parser
	 */
	public GLAccountGroupMasterData(CoreDriver coreDriver,
			MasterDataIdentity id, String descp) {
		super(coreDriver, id, descp, PARSER);
	}
}

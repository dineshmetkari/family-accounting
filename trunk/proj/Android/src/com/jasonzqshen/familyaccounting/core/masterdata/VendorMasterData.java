package com.jasonzqshen.familyaccounting.core.masterdata;

import org.w3c.dom.Element;

import com.jasonzqshen.familyaccounting.core.CoreDriver;

public class VendorMasterData extends MasterDataBase {
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
			VendorMasterData vendor = new VendorMasterData(coreDriver,
					identity, descp);
			return vendor;
		}
	};

	/**
	 * 
	 * @param id
	 * @param descp
	 * @param parser
	 */
	public VendorMasterData(CoreDriver coreDriver, MasterDataIdentity id,
			String descp) {
		super(coreDriver, id, descp, PARSER);
	}
}

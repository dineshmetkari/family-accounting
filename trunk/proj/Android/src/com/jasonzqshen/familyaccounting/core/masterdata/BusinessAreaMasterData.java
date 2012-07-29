package com.jasonzqshen.familyaccounting.core.masterdata;

import org.w3c.dom.Element;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.utils.CriticalLevel;

public class BusinessAreaMasterData extends MasterDataBase {
	/**
	 * parser
	 */
	public static IMasterDataParser PARSER = new IMasterDataParser() {
		public MasterDataBase parse(CoreDriver coreDriver, Element elem)
				throws Exception {
			String id = elem.getAttribute(MasterDataUtils.XML_ID);
			String descp = elem.getAttribute(MasterDataUtils.XML_DESCP);
			String criticalLevel = elem
					.getAttribute(MasterDataUtils.XML_CRITICAL_LEVEL);

			MasterDataIdentity identity = new MasterDataIdentity(
					id.toCharArray());
			BusinessAreaMasterData businessArea = new BusinessAreaMasterData(
					coreDriver, identity, descp);
			// set critical level
			if (criticalLevel != null) {
				CriticalLevel l = Enum.valueOf(CriticalLevel.class,
						criticalLevel);
				businessArea._criticalLevel = l;
			}
			return businessArea;
		}
	};

	private CriticalLevel _criticalLevel;

	/**
	 * 
	 * @param id
	 * @param descp
	 * @param parser
	 */
	public BusinessAreaMasterData(CoreDriver coreDriver,
			MasterDataIdentity id, String descp) {
		super(coreDriver, id, descp, PARSER);
	}

	/**
	 * set critical level
	 * 
	 * @param l
	 *            critical level
	 */
	public void setCriticalLevel(CriticalLevel l) {
		_criticalLevel = l;
	}

	/**
	 * get critical level
	 * 
	 * @return critical level
	 */
	public CriticalLevel getCriticalLevel() {
		return _criticalLevel;
	}
}

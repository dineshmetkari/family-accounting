package com.jasonzqshen.familyaccounting.core.masterdata;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.utils.CriticalLevel;

public class BusinessAreaMasterData extends MasterDataBase {
	public static final String FILE_NAME = "business.xml";

	private CriticalLevel _criticalLevel;

	/**
	 * 
	 * @param id
	 * @param descp
	 * @param parser
	 * @throws NullValueNotAcceptable
	 */
	public BusinessAreaMasterData(CoreDriver coreDriver, MasterDataIdentity id,
			String descp, CriticalLevel level) throws NullValueNotAcceptable {
		super(coreDriver, id, descp);

		setCriticalLevel(level);
	}

	/**
	 * set critical level
	 * 
	 * @param l
	 *            critical level
	 * @throws NullValueNotAcceptable
	 */
	public void setCriticalLevel(CriticalLevel l) throws NullValueNotAcceptable {
		if (l == null) {
			throw new NullValueNotAcceptable("Critical Level");
		}
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

	@Override
	public String toXML() {
		String superStr = super.toXML();
		return String.format("%s %s=\"%s\"", superStr,
				MasterDataUtils.XML_CRITICAL_LEVEL, _criticalLevel);
	}
}

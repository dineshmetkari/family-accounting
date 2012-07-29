package com.jasonzqshen.familyAccountingBackendTest;

import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;

public class TestUtilities {
	private TestUtilities() {
	}

	public static final String TEST_MASTER_DATA_FOLDER = "";

	public static final String[] GL_IDS = { "113100" };
	public static final String[] VENDOR_IDS = { "SUBWAY", "BUS" };
	public static final String[] CUSTOMER_IDS = { "MS", "SAP" };
	public static final String[] BUSINESS_IDS = { "WORK" };
	public static final String[] BANK_KEY_IDS = { "CMB", "SPDB" };
	public static final String[] BANK_ACCOUNT_IDS = { "CMB_6620", "SPDB_3704" };
	public static final String[] GL_GROUP_IDS = { "1130" };

	/**
	 * 
	 * @param type
	 * @param id
	 * @return
	 */
	public static boolean containsID(MasterDataType type, MasterDataIdentity id) {
		String[] data = null;
		if (type == MasterDataType.BANK_ACCOUNT) {
			data = BANK_ACCOUNT_IDS;
		} else if (type == MasterDataType.BANK_KEY) {
			data = BANK_KEY_IDS;
		} else if (type == MasterDataType.CUSTOMER) {
			data = CUSTOMER_IDS;
		} else if (type == MasterDataType.BUSINESS_AREA) {
			data = BUSINESS_IDS;
		} else if (type == MasterDataType.GL_ACCOUNT) {
			data = GL_IDS;
		} else if (type == MasterDataType.GL_ACCOUNT_GROUP) {
			data = GL_GROUP_IDS;
		} else if (type == MasterDataType.VENDOR) {
			data = VENDOR_IDS;
		}
		for (String str : data) {
			if (id.toString().equals(str)) {
				return true;
			}
		}
		return false;
	}
}

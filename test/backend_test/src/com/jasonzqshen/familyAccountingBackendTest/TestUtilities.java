package com.jasonzqshen.familyAccountingBackendTest;

import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.utils.BankAccountType;
import com.jasonzqshen.familyaccounting.core.utils.CriticalLevel;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountType;

public class TestUtilities {
	private TestUtilities() {
	}

	public static final String TEST_ROOT_FOLDER = "C:/FamilyAccountingTestData/test_data";
	public static final String TEST_ROOT_FOLDER_EMPTY = "./test_data";
	public static final String TEST_BANK_KEY = "CMB";
	public static final String TEST_ACCOUNT_NUMBER = "1234123412341234";
	public static final BankAccountType TEST_BANK_ACCOUNT_TYPE = BankAccountType.SAVING_ACCOUNT;
	public static final CriticalLevel TEST_CRITICAL_LEVEL = CriticalLevel.LOW;
	public static final String TEST_BANK_KEY_ID = "CMB_6620";
	public static final String TEST_GL_ACCOUNT_GROUP = "1010";
	public static final GLAccountType TEST_GL_ACCOUNT_TYPE = GLAccountType.BALANCE;

	public static final String TEST_DESCP = "test";

	public static final String[] GL_IDS = { "0000101001", "0000101002",
			"0000104001", "0000104002", "0000107001", "0000140001",
			"0000140002", "0000270801", "0000400001", "0000400002",
			"0000406001", "0000406002", };
	public static final String[] VENDOR_IDS = { "SUBWAY", "BUS" };
	public static final String[] CUSTOMER_IDS = { "MS", "SAP" };
	public static final String[] BUSINESS_IDS = { "WORK", "ENTERTAIN",
			"FAMILY", "TEAM_MATES", "FRIENDS", "SNACKS", "HEALTH",
			"DAILY_LIFE", "LUX_LIFE" };
	public static final String[] BANK_KEY_IDS = { "CMB", "SPDB", "ICBC" };
	public static final String[] BANK_ACCOUNT_IDS = { "CMB_6620", "CMB_6235",
			"SPDB_3704", "ICBC_0000" };
	public static final String[] GL_GROUP_IDS = { "1010", "1040", "1060",
			"1070", "1400", "1500", "1510", "1520", "1550", "2480", "2708",
			"2740", "4000", "4020", "4060", "4080", "5000", "5001", "5002",
			"5003" };

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
			try {
				MasterDataIdentity newId = new MasterDataIdentity(
						str.toCharArray());
				if (newId.equals(id)) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}

		}
		return false;
	}
}

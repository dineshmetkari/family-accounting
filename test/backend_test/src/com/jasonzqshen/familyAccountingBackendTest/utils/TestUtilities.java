package com.jasonzqshen.familyAccountingBackendTest.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.FiscalMonthRangeException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalYearRangeException;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityExists;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NoMasterDataFileException;
import com.jasonzqshen.familyaccounting.core.exception.ParametersException;
import com.jasonzqshen.familyaccounting.core.exception.RootFolderNotExsits;
import com.jasonzqshen.familyaccounting.core.exception.format.MasterDataFileFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.NoMasterDataFactoryClass;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.*;
import com.jasonzqshen.familyaccounting.core.utils.BankAccountType;
import com.jasonzqshen.familyaccounting.core.utils.CriticalLevel;
import com.jasonzqshen.familyaccounting.core.utils.DebugInformation;

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
	public static final String TEST_DOC_NUM = "1000000000";
	public static final String TEST_DOC_ID = "1000000000_2012_07";

	public static final String TEST_DESCP = "test";

	public static final String[] GL_IDS = { "1000100001", "1000100002",
			"1010100001", "1010100002", "1010100003", "1010100004",
			"1060100001", "1060100002", "1060100003", "1060100004",
			"1060100005", "1430100001", "1430100002", "1500100001",
			"1500100002", "2000100001", "2000100002", "3010100001",
			"4000100001", "4000100002", "4010100001", "4010100002",
			"4010100003", "4010100004", "5000100001", "5000100002",
			"5000100003", "5000100004", "5000100005", "5000100006",
			"5000100007" };
	public static final String[] VENDOR_IDS = { "SUBWAY", "BUS" };
	public static final String[] CUSTOMER_IDS = { "MS", "SAP" };
	public static final String[] BUSINESS_IDS = { "WORK", "ENTERTAIN",
			"FAMILY", "TEAM_MATES", "FRIENDS", "SNACKS", "HEALTH",
			"DAILY_LIFE", "LUX_LIFE" };
	public static final String[] BANK_KEY_IDS = { "CMB", "SPDB", "ICBC" };
	public static final String[] BANK_ACCOUNT_IDS = { "CMB_6620", "CMB_1002",
			"CMB_6235", "CMB_1001", "SPDB_3704", "ICBC_0001", "ICBC_1001" };
	public static final String[] DOCUMNET_NUMS = { "1000000001", "1000000002" };
	public static final String GL_ACCOUNT1 = "1000100001";
	public static final String GL_ACCOUNT2 = "1000100002";
	public static final String GL_ACCOUNT_COST = "5000100001";
	public static final String GL_ACCOUNT_PROFIT = "4010100001";
	public static final double AMOUNT = 100;
	public static final String VENDOR = "0000000BUS";
	public static final String CUSTOMER = "00000000MS";
	public static final String BUSINESS_AREA = "000000WORK";

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

	/**
	 * clear test root folder
	 */
	public static File clearTestingRootFolder() {
		// set the root folder
		File rootFolder = new File(TestUtilities.TEST_ROOT_FOLDER_EMPTY);
		if (!rootFolder.exists()) {
			rootFolder.mkdir();
		}

		deleteFileInFolder(rootFolder);
		return rootFolder;
	}

	private static void deleteFileInFolder(File folder) {
		if (folder.isFile()) {
			return;
		}

		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				deleteFileInFolder(f);
			}
			f.delete();
		}
	}

	/**
	 * 
	 * @return
	 * @throws RootFolderNotExsits
	 * @throws SystemException
	 * @throws NoMasterDataFactoryClass
	 * @throws IdentityInvalidChar
	 * @throws IdentityNoData
	 * @throws IdentityTooLong
	 * @throws MasterDataIdentityExists
	 * @throws ParametersException
	 * @throws MasterDataIdentityNotDefined
	 * @throws FiscalMonthRangeException
	 * @throws FiscalYearRangeException
	 * @throws MasterDataFileFormatException
	 * @throws NoMasterDataFileException
	 */
	public static CoreDriver establishMasterData(CoreDriver coreDriver)
			throws NoMasterDataFactoryClass, SystemException,
			RootFolderNotExsits, IdentityTooLong, IdentityNoData,
			IdentityInvalidChar, ParametersException, MasterDataIdentityExists,
			MasterDataIdentityNotDefined, FiscalYearRangeException,
			FiscalMonthRangeException, NoMasterDataFileException,
			MasterDataFileFormatException {

		/**
		 * check the factory is initialized, and the factory with no master data
		 * entities
		 */
		MasterDataManagement masterDataManagement = coreDriver
				.getMasterDataManagement();

		// vendor
		VendorMasterDataFactory vendorFactory = (VendorMasterDataFactory) masterDataManagement
				.getMasterDataFactory(MasterDataType.VENDOR);
		assertEquals(0, vendorFactory.getMasterDataCount());
		// customer
		CustomerMasterDataFactory customerFactory = (CustomerMasterDataFactory) masterDataManagement
				.getMasterDataFactory(MasterDataType.CUSTOMER);
		assertEquals(0, customerFactory.getMasterDataCount());
		// business area
		BusinessAreaMasterDataFactory businessFactory = (BusinessAreaMasterDataFactory) masterDataManagement
				.getMasterDataFactory(MasterDataType.BUSINESS_AREA);
		assertEquals(0, businessFactory.getMasterDataCount());
		// bank key
		BankKeyMasterDataFactory bankKeyFactory = (BankKeyMasterDataFactory) masterDataManagement
				.getMasterDataFactory(MasterDataType.BANK_KEY);
		assertEquals(0, bankKeyFactory.getMasterDataCount());
		// bank account
		BankAccountMasterDataFactory bankAccountFactory = (BankAccountMasterDataFactory) masterDataManagement
				.getMasterDataFactory(MasterDataType.BANK_ACCOUNT);
		assertEquals(0, bankAccountFactory.getMasterDataCount());
		// GL account
		GLAccountMasterDataFactory accountFactory = (GLAccountMasterDataFactory) masterDataManagement
				.getMasterDataFactory(MasterDataType.GL_ACCOUNT);
		assertEquals(0, accountFactory.getMasterDataCount());

		/** add master data entities */
		// vendor
		for (String str : TestUtilities.VENDOR_IDS) {
			VendorMasterData vendor = (VendorMasterData) vendorFactory
					.createNewMasterDataBase(
							new MasterDataIdentity(str.toCharArray()),
							TestUtilities.TEST_DESCP);
			assertTrue(vendor != null);
		}
		// duplicate id
		for (String str : TestUtilities.VENDOR_IDS) {
			try {
				VendorMasterData vendor = (VendorMasterData) vendorFactory
						.createNewMasterDataBase(
								new MasterDataIdentity(str.toCharArray()),
								TestUtilities.TEST_DESCP);
				assertTrue(false);
				assertEquals(null, vendor);
			} catch (MasterDataIdentityExists e) {

			}
		}

		// customer
		for (String str : TestUtilities.CUSTOMER_IDS) {
			CustomerMasterData customer = (CustomerMasterData) customerFactory
					.createNewMasterDataBase(
							new MasterDataIdentity(str.toCharArray()),
							TestUtilities.TEST_DESCP);
			assertTrue(customer != null);
		}
		// duplicate id
		for (String str : TestUtilities.CUSTOMER_IDS) {
			try {
				CustomerMasterData customer = (CustomerMasterData) customerFactory
						.createNewMasterDataBase(
								new MasterDataIdentity(str.toCharArray()),
								TestUtilities.TEST_DESCP);

				assertTrue(false);
				assertEquals(null, customer);
			} catch (MasterDataIdentityExists e) {
			}
		}

		// bank key
		for (String str : TestUtilities.BANK_KEY_IDS) {
			BankKeyMasterData bankKey = (BankKeyMasterData) bankKeyFactory
					.createNewMasterDataBase(
							new MasterDataIdentity(str.toCharArray()),
							TestUtilities.TEST_DESCP);
			assertTrue(bankKey != null);
		}
		// duplicate id
		for (String str : TestUtilities.BANK_KEY_IDS) {
			try {
				BankKeyMasterData bankKey = (BankKeyMasterData) bankKeyFactory
						.createNewMasterDataBase(
								new MasterDataIdentity(str.toCharArray()),
								TestUtilities.TEST_DESCP);
				assertTrue(false);
				assertEquals(null, bankKey);
			} catch (MasterDataIdentityExists e) {
			}
		}

		// bank account
		for (String str : TestUtilities.BANK_ACCOUNT_IDS) {
			MasterDataIdentity bankKey = new MasterDataIdentity(
					TestUtilities.TEST_BANK_KEY.toCharArray());
			BankAccountNumber accountNum = new BankAccountNumber(
					TestUtilities.TEST_ACCOUNT_NUMBER.toCharArray());
			BankAccountMasterData vendor = (BankAccountMasterData) bankAccountFactory
					.createNewMasterDataBase(
							new MasterDataIdentity(str.toCharArray()),
							TestUtilities.TEST_DESCP, accountNum, bankKey,
							BankAccountType.SAVING_ACCOUNT);
			assertTrue(vendor != null);
		}
		// duplicate id
		for (String str : TestUtilities.BANK_ACCOUNT_IDS) {
			try {
				MasterDataIdentity bankKey = new MasterDataIdentity(
						TestUtilities.TEST_BANK_KEY.toCharArray());
				BankAccountNumber accountNum = new BankAccountNumber(
						TestUtilities.TEST_ACCOUNT_NUMBER.toCharArray());
				BankAccountMasterData vendor = (BankAccountMasterData) bankAccountFactory
						.createNewMasterDataBase(
								new MasterDataIdentity(str.toCharArray()),
								TestUtilities.TEST_DESCP, accountNum, bankKey,
								TestUtilities.TEST_BANK_ACCOUNT_TYPE);
				assertTrue(false);
				assertEquals(null, vendor);
			} catch (MasterDataIdentityExists e) {
			}
		}

		// business area
		for (String str : TestUtilities.BUSINESS_IDS) {
			BusinessAreaMasterData businessArea = (BusinessAreaMasterData) businessFactory
					.createNewMasterDataBase(
							new MasterDataIdentity(str.toCharArray()),
							TestUtilities.TEST_DESCP,
							TestUtilities.TEST_CRITICAL_LEVEL);
			assertTrue(businessArea != null);
		}
		// duplicate id
		for (String str : TestUtilities.BUSINESS_IDS) {
			try {
				BusinessAreaMasterData businessArea = (BusinessAreaMasterData) businessFactory
						.createNewMasterDataBase(
								new MasterDataIdentity(str.toCharArray()),
								TestUtilities.TEST_DESCP,
								TestUtilities.TEST_CRITICAL_LEVEL);
				assertTrue(false);
				assertEquals(null, businessArea);
			} catch (MasterDataIdentityExists e) {
			}
		}

		// G/L account
		for (String str : TestUtilities.GL_IDS) {
			GLAccountMasterData glAccount = (GLAccountMasterData) accountFactory
					.createNewMasterDataBase(new MasterDataIdentity_GLAccount(
							str.toCharArray()), TestUtilities.TEST_DESCP);
			assertTrue(glAccount != null);
		}
		// duplicate id
		for (String str : TestUtilities.GL_IDS) {
			try {
				GLAccountMasterData glAccount = (GLAccountMasterData) accountFactory
						.createNewMasterDataBase(
								new MasterDataIdentity_GLAccount(str
										.toCharArray()),
								TestUtilities.TEST_DESCP);
				assertTrue(false);
				assertEquals(null, glAccount);
			} catch (MasterDataIdentityExists e) {
			}
		}
		return coreDriver;
	}

	public static void saveLogFile(String fileName, CoreDriver coreDriver) {
		StringBuilder strBuilder = new StringBuilder();
		for (DebugInformation info : coreDriver.getDebugInfos()) {
			strBuilder.append(info.toString());
			strBuilder.append("\n");
		}

		File file = new File(fileName);
		FileWriter writer;
		try {
			writer = new FileWriter(file);
			writer.write(strBuilder.toString());
			writer.close();
		} catch (IOException e) {
			throw new SystemException(e);
		}
	}

	
	

}

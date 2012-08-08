package com.jasonzqshen.familyAccountingBackendTest;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.FiscalMonthRangeException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalYearRangeException;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataFileFormatException;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityExists;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NoMasterDataFileException;
import com.jasonzqshen.familyaccounting.core.exception.ParametersException;
import com.jasonzqshen.familyaccounting.core.exception.RootFolderNotExsits;
import com.jasonzqshen.familyaccounting.core.exception.runtime.NoMasterDataFactoryClass;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.BankAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.BankAccountMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.BankAccountNumber;
import com.jasonzqshen.familyaccounting.core.masterdata.BankKeyMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.BankKeyMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.BusinessAreaMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.BusinessAreaMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.CustomerMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.CustomerMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountGroupMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountGroupMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.masterdata.VendorMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.VendorMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;

public class MasterDataTestCases {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * test identity
	 */
	@Test
	public void testMasterDataIdentity() {
		// test length
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i <= 10; ++i) {
			builder.append('A');
			try {
				char[] testCase = builder.toString().toCharArray();
				MasterDataIdentity test = new MasterDataIdentity(testCase);
				test.toString();
			} catch (Exception e) {
				fail(String.format("Length %d: %s", i, e.toString()));
			}
		}

		// test low case
		try {
			char[] testCase = "abcdefg".toCharArray();
			MasterDataIdentity test = new MasterDataIdentity(testCase);
			assertEquals(test.toString(), "000ABCDEFG");
		} catch (Exception e) {
			fail(e.toString());
		}

		// test number
		try {
			char[] testCase = "123456789".toCharArray();
			MasterDataIdentity test = new MasterDataIdentity(testCase);
			assertEquals(test.toString(), "0123456789");
		} catch (Exception e) {
			fail(e.toString());
		}

		// test '_'
		try {
			char[] testCase = "_123456789".toCharArray();
			MasterDataIdentity test = new MasterDataIdentity(testCase);
			assertEquals(test.toString(), "_123456789");
		} catch (Exception e) {
			fail(e.toString());
		}

		// test equals
		try {
			char[] testCase = "123".toCharArray();
			MasterDataIdentity test1 = new MasterDataIdentity(testCase);
			MasterDataIdentity test2 = new MasterDataIdentity(testCase);
			assertEquals(test1, test2);
			assertEquals(test1.hashCode(), test2.hashCode());
		} catch (Exception e) {
			fail(e.toString());
		}

		// zero length
		try {
			char[] zeroTest = "".toCharArray();
			MasterDataIdentity test = new MasterDataIdentity(zeroTest);
			test.toString();
			fail("IdentityNoData should occur when length is zero");
		} catch (IdentityNoData e) {
			// pass
		} catch (Exception e) {
			fail("IdentityNoData should occur when length is zero, but it is other exception");
		}

		// no data
		try {
			char[] noDataTest = "00".toCharArray();
			MasterDataIdentity test = new MasterDataIdentity(noDataTest);
			test.toString();
			fail("IdentityNoData should occur when there is no data");
		} catch (IdentityNoData e) {
			// pass
		} catch (Exception e) {
			fail("IdentityNoData should occur when length is zero, but it is other exception");
		}

		// format error
		try {
			char[] testCase = "a?".toCharArray();
			MasterDataIdentity test = new MasterDataIdentity(testCase);
			test.toString();
			fail("IdentityNoData should occur when there is no data");
		} catch (IdentityInvalidChar e) {
			// pass
		} catch (Exception e) {
			fail("IdentityInvalidChar should occur when length is zero, but it is other exception");
		}

		// too long
		try {
			char[] testCase = "12345678901".toCharArray();
			MasterDataIdentity test = new MasterDataIdentity(testCase);
			test.toString();
			fail("IdentityNoData should occur when there is no data");
		} catch (IdentityTooLong e) {
			// pass
		} catch (Exception e) {
			fail("IdentityInvalidChar should occur when length is zero, but it is other exception");
		}

		// identity for gl account
		try {
			char[] testCase = "113100".toCharArray();
			MasterDataIdentity_GLAccount test = new MasterDataIdentity_GLAccount(
					testCase);
			assertEquals(test.toString(), "0000113100");
		} catch (Exception e) {
			fail(e.toString());
		}

		// identity for gl account
		// format error
		try {
			char[] testCase = "abcd".toCharArray();
			MasterDataIdentity_GLAccount test = new MasterDataIdentity_GLAccount(
					testCase);
			test.toString();
			fail("IdentityNoData should occur when there is no data");
		} catch (IdentityInvalidChar e) {
			// pass
		} catch (Exception e) {
			fail("IdentityInvalidChar should occur when length is zero, but it is other exception");
		}
	}

	/**
	 * test identity
	 */
	@Test
	public void testBankAccountNumber() {
		// test length
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i <= 16; ++i) {
			builder.append('0');
			try {
				char[] testCase = builder.toString().toCharArray();
				BankAccountNumber test = new BankAccountNumber(testCase);
				test.toString();
			} catch (Exception e) {
				fail(String.format("Length %d: %s", i, e.toString()));
			}
		}

		// test number
		try {
			char[] testCase = "123456789".toCharArray();
			BankAccountNumber test = new BankAccountNumber(testCase);
			assertEquals(test.toString(), "0000000123456789");
		} catch (Exception e) {
			fail(e.toString());
		}

		// test number
		try {
			char[] testCase = "0000000000000000".toCharArray();
			BankAccountNumber test = new BankAccountNumber(testCase);
			assertEquals(test.toString(), "0000000000000000");
		} catch (Exception e) {
			fail(e.toString());
		}

		// test equals
		try {
			char[] testCase = "123".toCharArray();
			BankAccountNumber test1 = new BankAccountNumber(testCase);
			BankAccountNumber test2 = new BankAccountNumber(testCase);
			assertEquals(test1, test2);
			assertEquals(test1.hashCode(), test2.hashCode());
		} catch (Exception e) {
			fail(e.toString());
		}

		// zero length
		try {
			char[] zeroTest = "".toCharArray();
			BankAccountNumber test = new BankAccountNumber(zeroTest);
			test.toString();
			fail("IdentityNoData should occur when length is zero");
		} catch (IdentityNoData e) {
			// pass
		} catch (Exception e) {
			fail("IdentityNoData should occur when length is zero, but it is other exception");
		}

		// format error
		try {
			char[] testCase = "a?".toCharArray();
			BankAccountNumber test = new BankAccountNumber(testCase);
			test.toString();
			fail("IdentityNoData should occur when there is no data");
		} catch (IdentityInvalidChar e) {
			// pass
		} catch (Exception e) {
			fail("IdentityInvalidChar should occur when length is zero, but it is other exception");
		}

		// too long
		try {
			char[] testCase = "12345678901234567".toCharArray();
			BankAccountNumber test = new BankAccountNumber(testCase);
			test.toString();
			fail("IdentityNoData should occur when there is no data");
		} catch (IdentityTooLong e) {
			// pass
		} catch (Exception e) {
			fail("IdentityInvalidChar should occur when length is zero, but it is other exception");
		}
	}

	/**
	 * test master data loading
	 * 
	 * @throws Exception
	 * 
	 * @throws SystemException
	 * @throws NoMasterDataFactoryClass
	 * @throws RootFolderNotExsits
	 * @throws MasterDataFileFormatException
	 * @throws NoMasterDataFileException
	 */
	@Test
	public void testMasterDataInit() throws Exception {
		CoreDriver coreDriver = CoreDriver.getInstance();
		coreDriver.clear();
		try {

			ArrayList<CoreMessage> messages = new ArrayList<CoreMessage>();
			testMasterDataLoad(TestUtilities.TEST_ROOT_FOLDER, messages);
			assertEquals(0, messages.size());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {

			TestUtilities.saveLogFile("testMasterDataInit.txt", coreDriver);
		}
	}

	/**
	 * 
	 * @param rootFile
	 * @throws SystemException
	 * @throws NoMasterDataFactoryClass
	 * @throws RootFolderNotExsits
	 * @throws MasterDataFileFormatException
	 * @throws NoMasterDataFileException
	 */
	public void testMasterDataLoad(String rootFile,
			ArrayList<CoreMessage> messages) throws NoMasterDataFactoryClass,
			SystemException, RootFolderNotExsits, NoMasterDataFileException,
			MasterDataFileFormatException {
		CoreDriver coreDriver = CoreDriver.getInstance();

		// set root path
		coreDriver.setRootPath(rootFile);

		// initialize
		coreDriver.init(messages);
		if (messages.size() > 0) {
			for (CoreMessage m : messages) {
				System.out.println(m.toString());
			}
		}

		MasterDataManagement masterDataManagement = coreDriver
				.getMasterDataManagement();

		MasterDataBase[] datas;

		// check vendor data
		VendorMasterDataFactory vendorFactory = (VendorMasterDataFactory) masterDataManagement
				.getMasterDataFactory(MasterDataType.VENDOR);
		assertEquals(TestUtilities.VENDOR_IDS.length,
				vendorFactory.getMasterDataCount());
		datas = vendorFactory.getAllEntities();
		for (MasterDataBase data : datas) {
			VendorMasterData vendor = (VendorMasterData) data;
			assertTrue(TestUtilities.containsID(MasterDataType.VENDOR,
					vendor.getIdentity()));
			assertTrue(vendor.getDescp() != null);
		}

		// check customer data
		CustomerMasterDataFactory customerFactory = (CustomerMasterDataFactory) masterDataManagement
				.getMasterDataFactory(MasterDataType.CUSTOMER);
		assertEquals(TestUtilities.CUSTOMER_IDS.length,
				customerFactory.getMasterDataCount());
		datas = customerFactory.getAllEntities();
		for (MasterDataBase data : datas) {
			CustomerMasterData customer = (CustomerMasterData) data;
			assertTrue(TestUtilities.containsID(MasterDataType.CUSTOMER,
					customer.getIdentity()));
			assertTrue(customer.getDescp() != null);
		}

		// check business area
		BusinessAreaMasterDataFactory businessAreaFactory = (BusinessAreaMasterDataFactory) masterDataManagement
				.getMasterDataFactory(MasterDataType.BUSINESS_AREA);
		assertEquals(TestUtilities.BUSINESS_IDS.length,
				businessAreaFactory.getMasterDataCount());
		datas = businessAreaFactory.getAllEntities();
		for (MasterDataBase data : datas) {
			BusinessAreaMasterData businessArea = (BusinessAreaMasterData) data;
			assertTrue(TestUtilities.containsID(MasterDataType.BUSINESS_AREA,
					businessArea.getIdentity()));
			assertTrue(businessArea.getDescp() != null);
			assertTrue(businessArea.getCriticalLevel() != null);
		}

		// check bank key
		BankKeyMasterDataFactory bankKeyFactory = (BankKeyMasterDataFactory) masterDataManagement
				.getMasterDataFactory(MasterDataType.BANK_KEY);
		assertEquals(bankKeyFactory.getMasterDataCount(),
				TestUtilities.BANK_KEY_IDS.length);
		datas = bankKeyFactory.getAllEntities();
		for (MasterDataBase data : datas) {
			BankKeyMasterData bankKey = (BankKeyMasterData) data;
			assertTrue(TestUtilities.containsID(MasterDataType.BANK_KEY,
					bankKey.getIdentity()));
			assertTrue(bankKey.getDescp() != null);
		}

		// check bank account
		BankAccountMasterDataFactory bankAccountFactory = (BankAccountMasterDataFactory) masterDataManagement
				.getMasterDataFactory(MasterDataType.BANK_ACCOUNT);
		assertEquals(TestUtilities.BANK_ACCOUNT_IDS.length,
				bankAccountFactory.getMasterDataCount());
		datas = bankAccountFactory.getAllEntities();
		for (MasterDataBase data : datas) {
			BankAccountMasterData bankAccount = (BankAccountMasterData) data;
			assertTrue(TestUtilities.containsID(MasterDataType.BANK_ACCOUNT,
					bankAccount.getIdentity()));
			assertTrue(bankAccount.getDescp() != null);
			assertTrue(bankAccount.getBankAccType() != null);
			assertTrue(bankAccount.getBankKey() != null);
			assertTrue(bankAccount.getBankAccountNumber() != null);
		}

		// check G/L group
		GLAccountGroupMasterDataFactory accGroupFactory = (GLAccountGroupMasterDataFactory) masterDataManagement
				.getMasterDataFactory(MasterDataType.GL_ACCOUNT_GROUP);
		assertEquals(accGroupFactory.getMasterDataCount(),
				TestUtilities.GL_GROUP_IDS.length);
		datas = accGroupFactory.getAllEntities();
		for (MasterDataBase data : datas) {
			GLAccountGroupMasterData group = (GLAccountGroupMasterData) data;
			assertTrue(TestUtilities.containsID(
					MasterDataType.GL_ACCOUNT_GROUP, group.getIdentity()));
			assertTrue(group.getDescp() != null);
		}

		// check G/L account
		GLAccountMasterDataFactory accountFactory = (GLAccountMasterDataFactory) masterDataManagement
				.getMasterDataFactory(MasterDataType.GL_ACCOUNT);
		assertEquals(accountFactory.getMasterDataCount(),
				TestUtilities.GL_IDS.length);
		datas = accountFactory.getAllEntities();
		for (MasterDataBase data : datas) {
			GLAccountMasterData account = (GLAccountMasterData) data;
			assertTrue(TestUtilities.containsID(MasterDataType.GL_ACCOUNT,
					account.getIdentity()));
			assertTrue(account.getDescp() != null);
			assertTrue(account.getAccountGroup() != null);
			assertTrue(account.getGLAccountType() != null);
		}

	}

	/**
	 * test master initialize with empty folder; add new master data entity;
	 * store them into empty folder; re-load from folder and then check;
	 * @throws Exception 
	 * 
	 * @throws SystemException
	 * @throws NoMasterDataFactoryClass
	 * @throws IdentityInvalidChar
	 * @throws IdentityNoData
	 * @throws IdentityTooLong
	 * @throws ParametersException
	 * @throws MasterDataIdentityExists
	 * @throws MasterDataIdentityNotDefined
	 * @throws RootFolderNotExsits
	 * @throws FiscalMonthRangeException
	 * @throws FiscalYearRangeException
	 * @throws MasterDataFileFormatException
	 * @throws NoMasterDataFileException
	 */
	@Test
	public void testMasterDataStore() throws Exception {
		CoreDriver coreDriver = CoreDriver.getInstance();
		coreDriver.clear();
		try {
			TestUtilities.clearTestingRootFolder();

			ArrayList<CoreMessage> messages = new ArrayList<CoreMessage>();
			TestUtilities.establishMasterData(messages);

			// store
			coreDriver.getMasterDataManagement().store();

			messages.clear();
			// reload & check
			testMasterDataLoad(TestUtilities.TEST_ROOT_FOLDER_EMPTY, messages);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TestUtilities.saveLogFile("testMasterDataStore.txt", coreDriver);
		}

	}
}

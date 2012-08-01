package com.jasonzqshen.familyAccountingBackendTest;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityExists;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NoMasterDataFactoryClass;
import com.jasonzqshen.familyaccounting.core.exception.ParametersException;
import com.jasonzqshen.familyaccounting.core.exception.RootFolderNotExsits;
import com.jasonzqshen.familyaccounting.core.exception.SystemException;
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
import com.jasonzqshen.familyaccounting.core.utils.BankAccountType;
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
	 * @throws SystemException
	 * @throws NoMasterDataFactoryClass
	 * @throws RootFolderNotExsits
	 */
	@Test
	public void testMasterDataInit() throws NoMasterDataFactoryClass,
			SystemException, RootFolderNotExsits {
		testMasterDataLoad(TestUtilities.TEST_ROOT_FOLDER);
	}

	/**
	 * 
	 * @param rootFile
	 * @throws SystemException
	 * @throws NoMasterDataFactoryClass
	 * @throws RootFolderNotExsits
	 */
	public void testMasterDataLoad(String rootFile)
			throws NoMasterDataFactoryClass, SystemException,
			RootFolderNotExsits {
		CoreDriver coreDriver = CoreDriver.getInstance();

		// set root path
		coreDriver.setRootPath(rootFile);

		// initialize
		ArrayList<CoreMessage> messages = new ArrayList<CoreMessage>();
		coreDriver.init(messages);
		if (messages.size() > 0) {
			for (CoreMessage m : messages) {
				System.out.println(m.toString());
			}
		}
		assertEquals(0, messages.size());

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
	 */
	@Test
	public void testMasterDataStore() throws NoMasterDataFactoryClass,
			SystemException, IdentityTooLong, IdentityNoData,
			IdentityInvalidChar, ParametersException, MasterDataIdentityExists,
			MasterDataIdentityNotDefined, RootFolderNotExsits {
		// set the root folder
		File rootFolder = new File(TestUtilities.TEST_ROOT_FOLDER_EMPTY);
		if (!rootFolder.exists()) {
			rootFolder.mkdir();
		}

		// clear the master data folder
		File masterDataFolder = new File(String.format("%s/%s",
				TestUtilities.TEST_ROOT_FOLDER_EMPTY,
				MasterDataManagement.MASTER_DATA_FOLDER));
		for (File f : masterDataFolder.listFiles()) {
			f.delete();
		}

		CoreDriver coreDriver = CoreDriver.getInstance();

		// set root path
		coreDriver.setRootPath(TestUtilities.TEST_ROOT_FOLDER_EMPTY);
		// initialize
		ArrayList<CoreMessage> messages = new ArrayList<CoreMessage>();
		coreDriver.init(messages);
		if (messages.size() > 0) {
			for (CoreMessage m : messages) {
				System.out.println(m.toString());
			}
		}

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
		// GL account group
		GLAccountGroupMasterDataFactory groupFactory = (GLAccountGroupMasterDataFactory) masterDataManagement
				.getMasterDataFactory(MasterDataType.GL_ACCOUNT_GROUP);
		assertEquals(0, groupFactory.getMasterDataCount());
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
			} catch (MasterDataIdentityExists e) {
			}
		}

		// G/L account group
		for (String str : TestUtilities.GL_GROUP_IDS) {
			GLAccountGroupMasterData group = (GLAccountGroupMasterData) groupFactory
					.createNewMasterDataBase(
							new MasterDataIdentity(str.toCharArray()),
							TestUtilities.TEST_DESCP);
			assertTrue(group != null);
		}
		// duplicate id
		for (String str : TestUtilities.GL_GROUP_IDS) {
			try {
				GLAccountGroupMasterData group = (GLAccountGroupMasterData) groupFactory
						.createNewMasterDataBase(
								new MasterDataIdentity(str.toCharArray()),
								TestUtilities.TEST_DESCP);
				assertTrue(false);
			} catch (MasterDataIdentityExists e) {
			}
		}

		// G/L account
		for (String str : TestUtilities.GL_IDS) {
			MasterDataIdentity group = new MasterDataIdentity(
					TestUtilities.TEST_GL_ACCOUNT_GROUP.toCharArray());
			GLAccountMasterData glAccount = (GLAccountMasterData) accountFactory
					.createNewMasterDataBase(new MasterDataIdentity_GLAccount(
							str.toCharArray()), TestUtilities.TEST_DESCP,
							TestUtilities.TEST_GL_ACCOUNT_TYPE, group);
			assertTrue(glAccount != null);
		}
		// duplicate id
		for (String str : TestUtilities.GL_IDS) {
			try {
				MasterDataIdentity group = new MasterDataIdentity(
						TestUtilities.TEST_GL_ACCOUNT_GROUP.toCharArray());
				GLAccountMasterData glAccount = (GLAccountMasterData) accountFactory
						.createNewMasterDataBase(
								new MasterDataIdentity_GLAccount(str
										.toCharArray()),
								TestUtilities.TEST_DESCP,
								TestUtilities.TEST_GL_ACCOUNT_TYPE, group);
				assertTrue(false);
			} catch (MasterDataIdentityExists e) {
			}
		}

		// store
		masterDataManagement.store();

		// reload & check
		testMasterDataLoad(TestUtilities.TEST_ROOT_FOLDER_EMPTY);
	}
}

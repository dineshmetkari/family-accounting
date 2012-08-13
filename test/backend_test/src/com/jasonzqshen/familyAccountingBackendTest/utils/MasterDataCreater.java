package com.jasonzqshen.familyAccountingBackendTest.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import com.jasonzqshen.familyaccounting.core.masterdata.BankAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.BankAccountMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.BankAccountNumber;
import com.jasonzqshen.familyaccounting.core.masterdata.BankKeyMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.BankKeyMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.BusinessAreaMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.BusinessAreaMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.CustomerMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.CustomerMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.masterdata.VendorMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.VendorMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.utils.BankAccountType;

public class MasterDataCreater {
	public static CoreDriver createMasterData(CoreDriver coreDriver)
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
}

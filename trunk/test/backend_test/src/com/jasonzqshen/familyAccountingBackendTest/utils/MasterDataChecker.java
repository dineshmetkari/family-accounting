package com.jasonzqshen.familyAccountingBackendTest.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.BankAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.BankAccountMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.BankKeyMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.BankKeyMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.BusinessAreaMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.BusinessAreaMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.CustomerMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.CustomerMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.masterdata.VendorMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.VendorMasterDataFactory;

public class MasterDataChecker {
	/**
	 * check the master data information
	 * 
	 * @param coreDriver
	 */
	public static void checkMasterData(CoreDriver coreDriver) {
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
		}

	}

}

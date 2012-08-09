package com.jasonzqshen.familyAccountingBackendTest;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.document_entries.CustomerEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.GLAccountEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.VendorEntry;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.AccountType;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;

public class DocumentEntriesTestCase {
	@Test
	public void testGLAccountEntry() throws Exception {
		CoreDriver coreDriver = CoreDriver.getInstance();
		coreDriver.clear();

		TransactionDataManagement transManagement = coreDriver
				.getTransDataManagement();
		try {
			ArrayList<CoreMessage> messages = new ArrayList<CoreMessage>();
			TestUtilities.clearTestingRootFolder();
			TestUtilities.establishMasterData(messages);

			SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
			Date date = format.parse("2012.07.02");

			/**
			 * test G/L account entry. move 100 RMB from account 0000270801 to
			 * 0000406002
			 */
			GLAccountEntry entry = new GLAccountEntry(coreDriver);
			entry.setValue(GLAccountEntry.AMOUNT, 100);
			entry.setValue(GLAccountEntry.POSTING_DATE, date);
			entry.setDstAccount(new MasterDataIdentity_GLAccount("0000406002"
					.toCharArray()));
			entry.setSourceAccount(new MasterDataIdentity_GLAccount(
					"0000270801".toCharArray()));
			entry.setValue(GLAccountEntry.TEXT, TestUtilities.TEST_DESCP);
			ArrayList<CoreMessage> msg = new ArrayList<CoreMessage>();
			entry.save(msg);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TestUtilities.saveLogFile("testTransactionDataStore.txt",
					coreDriver);
		}

		coreDriver.clear();

		// set root path
		coreDriver.setRootPath(TestUtilities.TEST_ROOT_FOLDER_EMPTY);
		coreDriver.setStartMonthID(new MonthIdentity(2012, 7));

		// initialize
		ArrayList<CoreMessage> messages = new ArrayList<CoreMessage>();
		coreDriver.init(messages);

		HeadEntity[] collection = transManagement.getDocs(2012, 7);
		assertEquals(1, collection.length);
		HeadEntity head = collection[0];
		assertEquals(DocumentType.GL, head.getDocumentType());
		assertEquals(TestUtilities.TEST_DESCP, head.getDocText());
		assertEquals(2, head.getItemCount());

		ItemEntity[] items = head.getItems();
		for (ItemEntity item : items) {
			if (item.getCDIndicator() == CreditDebitIndicator.CREDIT) {
				assertEquals("0000270801", item.getGLAccount().toString());
				assertEquals(AccountType.GL_ACCOUNT, item.getAccountType());
				assertEquals(100, (int) item.getAmount());
			} else {
				assertEquals("0000406002", item.getGLAccount().toString());
				assertEquals(AccountType.GL_ACCOUNT, item.getAccountType());
				assertEquals(100, (int) item.getAmount());
			}
		}
	}

	@Test
	public void testVendorEntry() throws Exception {
		CoreDriver coreDriver = CoreDriver.getInstance();
		coreDriver.clear();

		TransactionDataManagement transManagement = coreDriver
				.getTransDataManagement();
		try {
			ArrayList<CoreMessage> messages = new ArrayList<CoreMessage>();
			TestUtilities.clearTestingRootFolder();
			TestUtilities.establishMasterData(messages);

			SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
			Date date = format.parse("2012.07.02");

			VendorEntry entry = new VendorEntry(coreDriver);
			entry.setValue(VendorEntry.AMOUNT, 100);
			entry.setValue(VendorEntry.POSTING_DATE, date);
			entry.setValue(
					VendorEntry.REC_ACC,
					new MasterDataIdentity_GLAccount("0000406002".toCharArray()));
			entry.setValue(
					VendorEntry.GL_ACCOUNT,
					new MasterDataIdentity_GLAccount("0000270801".toCharArray()));
			entry.setValue(VendorEntry.VENDOR,
					new MasterDataIdentity("BUS".toCharArray()));
			entry.setValue(VendorEntry.TEXT, TestUtilities.TEST_DESCP);
			entry.setValue(VendorEntry.BUSINESS_AREA, new MasterDataIdentity(
					"0000SNACKS".toCharArray()));
			ArrayList<CoreMessage> msg = new ArrayList<CoreMessage>();
			entry.save(msg);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TestUtilities.saveLogFile("testTransactionDataStore.txt",
					coreDriver);
		}

		coreDriver.clear();

		// set root path
		coreDriver.setRootPath(TestUtilities.TEST_ROOT_FOLDER_EMPTY);
		coreDriver.setStartMonthID(new MonthIdentity(2012, 7));

		// initialize
		ArrayList<CoreMessage> messages = new ArrayList<CoreMessage>();
		coreDriver.init(messages);

		HeadEntity[] collection = transManagement.getDocs(2012, 7);
		assertEquals(1, collection.length);
		HeadEntity head = collection[0];
		assertEquals(DocumentType.VENDOR_INVOICE, head.getDocumentType());
		assertEquals(TestUtilities.TEST_DESCP, head.getDocText());
		assertEquals(2, head.getItemCount());

		ItemEntity[] items = head.getItems();
		for (ItemEntity item : items) {
			if (item.getCDIndicator() == CreditDebitIndicator.CREDIT) {
				assertEquals("0000406002", item.getGLAccount().toString());
				assertEquals("0000000BUS", item.getVendor().toString());
				assertEquals(AccountType.VENDOR, item.getAccountType());
				assertEquals(100, (int) item.getAmount());
			} else {
				assertEquals("0000270801", item.getGLAccount().toString());
				assertEquals(AccountType.GL_ACCOUNT, item.getAccountType());
				assertEquals(100, (int) item.getAmount());
				assertEquals("0000SNACKS", item.getBusinessArea().toString());
			}
		}
	}

	@Test
	public void testCustomerEntry() throws Exception {
		CoreDriver coreDriver = CoreDriver.getInstance();
		coreDriver.clear();

		TransactionDataManagement transManagement = coreDriver
				.getTransDataManagement();
		try {
			ArrayList<CoreMessage> messages = new ArrayList<CoreMessage>();
			TestUtilities.clearTestingRootFolder();
			TestUtilities.establishMasterData(messages);

			SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
			Date date = format.parse("2012.07.02");

			CustomerEntry entry = new CustomerEntry(coreDriver);
			entry.setValue(CustomerEntry.AMOUNT, 100);
			entry.setValue(CustomerEntry.POSTING_DATE, date);
			entry.setValue(
					CustomerEntry.REC_ACC,
					new MasterDataIdentity_GLAccount("0000406002".toCharArray()));
			entry.setValue(
					CustomerEntry.GL_ACCOUNT,
					new MasterDataIdentity_GLAccount("0000270801".toCharArray()));
			entry.setValue(CustomerEntry.CUSTOMER, new MasterDataIdentity(
					"00000000MS".toCharArray()));
			entry.setValue(CustomerEntry.TEXT, TestUtilities.TEST_DESCP);
			ArrayList<CoreMessage> msg = new ArrayList<CoreMessage>();
			entry.save(msg);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TestUtilities.saveLogFile("testTransactionDataStore.txt",
					coreDriver);
		}

		coreDriver.clear();

		// set root path
		coreDriver.setRootPath(TestUtilities.TEST_ROOT_FOLDER_EMPTY);
		coreDriver.setStartMonthID(new MonthIdentity(2012, 7));

		// initialize
		ArrayList<CoreMessage> messages = new ArrayList<CoreMessage>();
		coreDriver.init(messages);

		HeadEntity[] collection = transManagement.getDocs(2012, 7);
		assertEquals(1, collection.length);
		HeadEntity head = collection[0];
		assertEquals(DocumentType.CUSTOMER_INVOICE, head.getDocumentType());
		assertEquals(TestUtilities.TEST_DESCP, head.getDocText());
		assertEquals(2, head.getItemCount());

		ItemEntity[] items = head.getItems();
		for (ItemEntity item : items) {
			if (item.getCDIndicator() == CreditDebitIndicator.CREDIT) {
				assertEquals("0000270801", item.getGLAccount().toString());
				assertEquals(AccountType.GL_ACCOUNT, item.getAccountType());
				assertEquals(100, (int) item.getAmount());
			} else {
				assertEquals("0000406002", item.getGLAccount().toString());
				assertEquals("00000000MS", item.getCustomer().toString());
				assertEquals(AccountType.CUSTOMER, item.getAccountType());
				assertEquals(100, (int) item.getAmount());
			}
		}
	}
}

package com.jasonzqshen.familyAccountingBackendTest.entry;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jasonzqshen.familyAccountingBackendTest.utils.DocumentCreater;
import com.jasonzqshen.familyAccountingBackendTest.utils.MasterDataCreater;
import com.jasonzqshen.familyAccountingBackendTest.utils.TestData;
import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyAccountingBackendTest.utils.TransactionDataChecker;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.document_entries.CustomerEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.EntryTemplatesManagement;
import com.jasonzqshen.familyaccounting.core.document_entries.GLAccountEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.VendorEntry;
import com.jasonzqshen.familyaccounting.core.transaction.DocumentIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;

public class CreateWithTemplateTester extends TesterBase {
	EntryTemplatesManagement tempMgmt;

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		TestUtilities.establishFolder2012_07(
				TestUtilities.TEST_ROOT_CREATE_WITH_TEMPLATE, coreDriver);
		tempMgmt = new EntryTemplatesManagement(coreDriver);
		//coreDriver.setRootPath(TestUtilities.TEST_ROOT_CREATE_WITH_TEMPLATE);
		assertEquals(true, coreDriver.isInitialized());

		MasterDataCreater.createMasterData(coreDriver);

		// create templates

		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		// create document with template
		tempMgmt.initialize();
		TemplatesCreateTester.createTemplate(coreDriver, tempMgmt);

		Date date = format.parse("2012.07.02");

		VendorEntry vendor = (VendorEntry) tempMgmt.getEntryTemplate(1)
				.generateEntry();
		vendor.setValue(VendorEntry.POSTING_DATE, date);
		vendor.save(true);

		CustomerEntry customer = (CustomerEntry) tempMgmt.getEntryTemplate(3)
				.generateEntry();
		customer.setValue(CustomerEntry.POSTING_DATE, date);
		customer.setValue(CustomerEntry.AMOUNT, TestData.AMOUNT_CUSTOMER);
		customer.save(true);

		GLAccountEntry glEntry = (GLAccountEntry) tempMgmt.getEntryTemplate(2)
				.generateEntry();
		glEntry.setValue(GLAccountEntry.POSTING_DATE, date);
		glEntry.setValue(GLAccountEntry.AMOUNT, TestData.AMOUNT_GL);
		glEntry.save(true);

		TransactionDataManagement transManagement = coreDriver
				.getTransDataManagement();

		// month 08
		date = format.parse("2012.08.02");
		HeadEntity headEntity = DocumentCreater.createVendorDoc(coreDriver,
				date);
		DocumentIdentity docId = headEntity.getDocIdentity();
		transManagement.reverseDocument(docId);
	}

	@Override
	protected void check(CoreDriver coreDriver) throws Exception {
		TransactionDataChecker.checkTransactionData(coreDriver);

		// reload
		coreDriver.restart();
		TransactionDataChecker.checkTransactionData(coreDriver);

	}

}

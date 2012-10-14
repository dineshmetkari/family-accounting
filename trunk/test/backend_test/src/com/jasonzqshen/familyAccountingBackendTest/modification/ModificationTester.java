package com.jasonzqshen.familyAccountingBackendTest.modification;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jasonzqshen.familyAccountingBackendTest.utils.DocumentCreater;
import com.jasonzqshen.familyAccountingBackendTest.utils.MasterDataCreater;
import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.document_entries.VendorEntry;
import com.jasonzqshen.familyaccounting.core.transaction.DocumentIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;

public class ModificationTester extends TesterBase {

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		// -------------------------------------------------------
		// initialize test
		TestUtilities
				.establishFolder2012_07(TestUtilities.TEST_ROOT_LEDGER_CLOSING);
		coreDriver.setRootPath(TestUtilities.TEST_ROOT_LEDGER_CLOSING);
		assertEquals(true, coreDriver.isInitialized());

		// -------------------------------------------------------
		// create master data
		MasterDataCreater.createMasterData(coreDriver);

		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");

		// ------------------------------------------------------
		// create transaction data in month 07
		Date date = format.parse("2012.07.02");
		// ledger
		HeadEntity doc07 = DocumentCreater.createVendorDoc(coreDriver, date);
		DocumentCreater.createCustomerDoc(coreDriver, date);
		DocumentCreater.createGLDoc(coreDriver, date);

		// month close
		TransactionDataManagement transManagement = coreDriver
				.getTransDataManagement();
		transManagement.monthEndClose();

		coreDriver.restart();

		// create transaction in month 08
		date = format.parse("2012.08.02");
		HeadEntity headEntity = DocumentCreater.createVendorDoc(coreDriver,
				date);
		DocumentIdentity docId = headEntity.getDocIdentity();

		// reverse document
		transManagement.reverseDocument(docId);

		// -------------------------------------------------------
		// modify document in month 07
		VendorEntry entry07 = VendorEntry.parse(doc07);
		entry07.setValue(VendorEntry.AMOUNT, new CurrencyAmount(10.0));
		entry07.save(true);
	}

	@Override
	protected void check(CoreDriver coreDriver) throws Exception {

	}

}

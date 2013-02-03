package com.jasonzqshen.familyAccountingBackendTest.core;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jasonzqshen.familyAccountingBackendTest.utils.DocumentCreater;
import com.jasonzqshen.familyAccountingBackendTest.utils.MasterDataCreater;
import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyAccountingBackendTest.utils.TransactionDataChecker;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.transaction.DocumentIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;

public class TransactionCreationTester extends TesterBase {

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		TestUtilities.establishFolder2012_07(
				TestUtilities.TEST_ROOT_LEDGER_CLOSING, coreDriver);
		assertEquals(true, coreDriver.isInitialized());

		MasterDataCreater.createMasterData(coreDriver);

		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");

		// month 08, reverse document
		Date date = format.parse("2012.07.02");
		// ledger
		DocumentCreater.createVendorDoc(coreDriver, date);
		DocumentCreater.createCustomerDoc(coreDriver, date);
		DocumentCreater.createGLDoc(coreDriver, date);

		TransactionDataManagement transManagement = coreDriver
				.getTransDataManagement();

		coreDriver.restart();

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

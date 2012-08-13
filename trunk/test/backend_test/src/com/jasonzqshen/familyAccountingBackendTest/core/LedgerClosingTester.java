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

public class LedgerClosingTester extends TesterBase {

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		TestUtilities
				.establishFolder2012_07(TestUtilities.TEST_ROOT_LEDGER_CLOSING);
		coreDriver.setRootPath(TestUtilities.TEST_ROOT_LEDGER_CLOSING);
		assertEquals(true, coreDriver.isInitialized());

		MasterDataCreater.createMasterData(coreDriver);

		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");

		// month 08, reverse document
		Date date = format.parse("2012.07.02");
		// ledger
		DocumentCreater.createVendorDoc(coreDriver, date);
		DocumentCreater.createCustomerDoc(coreDriver, date);

		TransactionDataManagement transManagement = coreDriver
				.getTransDataManagement();
		transManagement.monthEndClose();

		date = format.parse("2012.08.02");
		HeadEntity headEntity = DocumentCreater.createVendorDoc(coreDriver,
				date);
		DocumentIdentity docId = headEntity.getDocIdentity();
		HeadEntity reverseEntity = transManagement.reverseDocument(docId);
		reverseEntity.setDocText(TestUtilities.TEST_DESCP);
		// store
		reverseEntity.save(true);
		TransactionDataChecker.checkTransactionData(coreDriver);
		
		// reload
		coreDriver.restart();
		TransactionDataChecker.checkTransactionData(coreDriver);
	}
}

package com.jasonzqshen.familyAccountingBackendTest.core;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jasonzqshen.familyAccountingBackendTest.utils.DocumentCreaterCN;
import com.jasonzqshen.familyAccountingBackendTest.utils.MasterDataCheckerCN;
import com.jasonzqshen.familyAccountingBackendTest.utils.MasterDataCreaterCN;
import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyAccountingBackendTest.utils.TransactionDataCheckerCN;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.transaction.DocumentIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.Language;

/**
 * creation test for Chinese
 * 
 * @author jasonzqshen@gmail.com
 * 
 */
public class TransactionCreationTesterCN extends TesterBase {

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		TestUtilities.establishFolder2012_07(
				TestUtilities.TEST_ROOT_LEDGER_CLOSING_CN, coreDriver);
		//coreDriver.setRootPath(TestUtilities.TEST_ROOT_LEDGER_CLOSING_CN);
		coreDriver.setLanguage(Language.SimpleChinese);
		assertEquals(true, coreDriver.isInitialized());

		MasterDataCreaterCN.createMasterData(coreDriver);
		// check master data
		MasterDataCheckerCN.checkMasterData(coreDriver);

		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");

		// month 08, reverse document
		Date date = format.parse("2012.07.02");
		// ledger
		DocumentCreaterCN.createVendorDoc(coreDriver, date);
		DocumentCreaterCN.createCustomerDoc(coreDriver, date);
		DocumentCreaterCN.createGLDoc(coreDriver, date);

		TransactionDataManagement transManagement = coreDriver
				.getTransDataManagement();

		coreDriver.restart();

		date = format.parse("2012.08.02");
		HeadEntity headEntity = DocumentCreaterCN.createVendorDoc(coreDriver,
				date);
		DocumentIdentity docId = headEntity.getDocIdentity();
		transManagement.reverseDocument(docId);

	}

	@Override
	protected void check(CoreDriver coreDriver) throws Exception {
		TransactionDataCheckerCN.checkTransactionData(coreDriver);

		// reload
		coreDriver.restart();
		TransactionDataCheckerCN.checkTransactionData(coreDriver);

	}
}

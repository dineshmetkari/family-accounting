package com.jasonzqshen.familyAccountingBackendTest.core;

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
import com.jasonzqshen.familyaccounting.core.document_entries.GLAccountEntry;
import com.jasonzqshen.familyaccounting.core.exception.SaveClosedLedgerException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.transaction.DocumentIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthLedger;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;

/**
 * reverse and create new document in closed ledger, SaveClosedLedgerException
 * must be raised and transaction data management should keep as no changed.
 * 
 * @author jasonzqshen@gmail.com
 * 
 */
public class CloseLedgerTester extends TesterBase {

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		TestUtilities
				.establishFolder2012_07(TestUtilities.TEST_ROOT_LEDGER_CLOSING);
		coreDriver.setRootPath(TestUtilities.TEST_ROOT_LEDGER_CLOSING);

		createdata(coreDriver);

		// -------------------------------------------------------
		// get closed ledger
		TransactionDataManagement transMgmt = coreDriver
				.getTransDataManagement();
		MonthLedger closedLedger = transMgmt.getLedger(new MonthIdentity(2012,
				07));

		// -------------------------------------------------------
		// reverse document
		HeadEntity doc = closedLedger.getEntities()[0];
		try {
			// should raise exception
			transMgmt.reverseDocument(doc.getDocIdentity());
			assertEquals(true, false);
		} catch (SaveClosedLedgerException e) {
			assertEquals(true, true);
		}

		// ------------------------------------------------------
		// check whether the memory has been changed
		TransactionDataChecker.checkTransactionData(coreDriver);

		// reload
		coreDriver.restart();
		TransactionDataChecker.checkTransactionData(coreDriver);

		// -------------------------------------------------------------
		// create new document
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		Date date = format.parse("2012.07.02");

		GLAccountEntry glAccEntry = new GLAccountEntry(coreDriver);
		glAccEntry.setValue(GLAccountEntry.POSTING_DATE, date);
		glAccEntry.setValue(GLAccountEntry.TEXT, TestData.TEXT_GL_DOC);
		glAccEntry.setValue(GLAccountEntry.SRC_ACCOUNT,
				new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_BANK));
		glAccEntry.setValue(GLAccountEntry.DST_ACCOUNT,
				new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_CASH));
		glAccEntry.setValue(GLAccountEntry.AMOUNT, TestData.AMOUNT_GL);

		try {
			// should raise exception
			glAccEntry.save(true);
			assertEquals(true, false);
		} catch (SaveClosedLedgerException e) {
			assertEquals(true, true);
		}
		// ------------------------------------------------------
		// check whether the memory has been changed
		TransactionDataChecker.checkTransactionData(coreDriver);

		// reload
		coreDriver.restart();
		TransactionDataChecker.checkTransactionData(coreDriver);
	}

	@Override
	protected void check(CoreDriver coreDriver) throws Exception {

	}

	/**
	 * create data
	 */
	private void createdata(CoreDriver coreDriver) throws Exception {
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
		transManagement.monthEndClose();

		coreDriver.restart();

		date = format.parse("2012.08.02");
		HeadEntity headEntity = DocumentCreater.createVendorDoc(coreDriver,
				date);
		DocumentIdentity docId = headEntity.getDocIdentity();
		transManagement.reverseDocument(docId);

		// ------------------------------------------------------
		// check
		TransactionDataChecker.checkTransactionData(coreDriver);

		// reload
		coreDriver.restart();
		TransactionDataChecker.checkTransactionData(coreDriver);
	}

}

package com.jasonzqshen.familyAccountingBackendTest.entry;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.document_entries.GLAccountEntry;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthLedger;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.AccountType;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;

public class GLEntryTester extends TesterBase {

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		coreDriver.setRootPath(TestUtilities.TEST_ROOT_FOLDER);

		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		Date date = format.parse("2012.08.02");

		/**
		 * test G/L account entry. move 100 RMB from account GL1 to GL2
		 */
		GLAccountEntry entry = new GLAccountEntry(coreDriver);
		entry.setValue(GLAccountEntry.AMOUNT, 100);
		entry.setValue(GLAccountEntry.POSTING_DATE, date);
		entry.setDstAccount(new MasterDataIdentity_GLAccount(
				TestUtilities.GL_ACCOUNT1.toCharArray()));
		entry.setSourceAccount(new MasterDataIdentity_GLAccount(
				TestUtilities.GL_ACCOUNT2.toCharArray()));
		entry.setValue(GLAccountEntry.TEXT, TestUtilities.TEST_DESCP);
		entry.save(false);

	}

	@Override
	protected void check(CoreDriver coreDriver) throws Exception {
		TransactionDataManagement transManagement = coreDriver
				.getTransDataManagement();
		MonthLedger ledger = transManagement.getLedger(2012, 8);
		assertEquals(3, ledger.getCount());

		HeadEntity[] collection = ledger.getEntities();
		HeadEntity head = collection[2];
		assertEquals(DocumentType.GL, head.getDocumentType());
		assertEquals(TestUtilities.TEST_DESCP, head.getDocText());
		assertEquals(2, head.getItemCount());

		ItemEntity[] items = head.getItems();
		for (ItemEntity item : items) {
			if (item.getCDIndicator() == CreditDebitIndicator.CREDIT) {
				assertEquals(TestUtilities.GL_ACCOUNT2, item.getGLAccount()
						.toString());
				assertEquals(AccountType.GL_ACCOUNT, item.getAccountType());
				assertEquals("100.00", item.getAmount().toString());
			} else {
				assertEquals(TestUtilities.GL_ACCOUNT1, item.getGLAccount()
						.toString());
				assertEquals(AccountType.GL_ACCOUNT, item.getAccountType());
				assertEquals("100.00", item.getAmount().toString());
			}
		}

	}

}

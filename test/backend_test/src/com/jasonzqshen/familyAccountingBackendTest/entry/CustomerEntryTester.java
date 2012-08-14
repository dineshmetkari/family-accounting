package com.jasonzqshen.familyAccountingBackendTest.entry;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.document_entries.CustomerEntry;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthLedger;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.AccountType;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;

public class CustomerEntryTester extends TesterBase {

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		coreDriver.setRootPath(TestUtilities.TEST_ROOT_FOLDER);

		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		Date date = format.parse("2012.08.02");

		CustomerEntry entry = new CustomerEntry(coreDriver);
		entry.setValue(CustomerEntry.AMOUNT, 100);
		entry.setValue(CustomerEntry.POSTING_DATE, date);
		entry.setValue(CustomerEntry.REC_ACC, new MasterDataIdentity_GLAccount(
				TestUtilities.GL_ACCOUNT1.toCharArray()));
		entry.setValue(CustomerEntry.GL_ACCOUNT,
				new MasterDataIdentity_GLAccount(
						TestUtilities.GL_ACCOUNT_PROFIT.toCharArray()));
		entry.setValue(CustomerEntry.CUSTOMER, new MasterDataIdentity(
				TestUtilities.CUSTOMER));
		entry.setValue(CustomerEntry.TEXT, TestUtilities.TEST_DESCP);
		entry.save(false);

	}

	@Override
	protected void check(CoreDriver coreDriver) throws Exception {
		TransactionDataManagement transManagement = coreDriver
				.getTransDataManagement();
		MonthLedger ledger = transManagement.getLedger(2012, 8);
		assertEquals(3, ledger.getCount());

		HeadEntity[] entities = ledger.getEntities();
		HeadEntity head = entities[2];
		assertEquals(DocumentType.CUSTOMER_INVOICE, head.getDocumentType());
		assertEquals(TestUtilities.TEST_DESCP, head.getDocText());
		assertEquals(2, head.getItemCount());

		ItemEntity[] items = head.getItems();
		for (ItemEntity item : items) {
			if (item.getCDIndicator() == CreditDebitIndicator.CREDIT) {
				assertEquals(TestUtilities.GL_ACCOUNT_PROFIT, item
						.getGLAccount().toString());
				assertEquals(AccountType.GL_ACCOUNT, item.getAccountType());
				assertEquals("100.00", item.getAmount().toString());
			} else {
				assertEquals(TestUtilities.GL_ACCOUNT1, item.getGLAccount()
						.toString());
				assertEquals(TestUtilities.CUSTOMER, item.getCustomer()
						.toString());
				assertEquals(AccountType.CUSTOMER, item.getAccountType());
				assertEquals("100.00", item.getAmount().toString());
			}
		}

	}

}

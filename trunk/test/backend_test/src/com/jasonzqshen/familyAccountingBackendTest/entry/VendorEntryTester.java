package com.jasonzqshen.familyAccountingBackendTest.entry;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.document_entries.VendorEntry;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthLedger;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.AccountType;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;

public class VendorEntryTester extends TesterBase {

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		coreDriver.setRootPath(TestUtilities.TEST_ROOT_FOLDER);

		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		Date date = format.parse("2012.08.02");

		VendorEntry entry = new VendorEntry(coreDriver);
		entry.setValue(VendorEntry.AMOUNT, 100);
		entry.setValue(VendorEntry.POSTING_DATE, date);
		entry.setValue(VendorEntry.REC_ACC, new MasterDataIdentity_GLAccount(
				TestUtilities.GL_ACCOUNT1));
		entry.setValue(VendorEntry.GL_ACCOUNT,
				new MasterDataIdentity_GLAccount(TestUtilities.GL_ACCOUNT_COST));
		entry.setValue(VendorEntry.VENDOR, new MasterDataIdentity(
				TestUtilities.VENDOR));
		entry.setValue(VendorEntry.TEXT, TestUtilities.TEST_DESCP);
		entry.setValue(VendorEntry.BUSINESS_AREA, new MasterDataIdentity(
				TestUtilities.BUSINESS_AREA));

		entry.save(false);

		TransactionDataManagement transManagement = coreDriver
				.getTransDataManagement();
		MonthLedger ledger = transManagement.getLedger(2012, 8);
		assertEquals(3, ledger.getCount());

		HeadEntity[] collection = ledger.getEntities();
		HeadEntity head = collection[2];
		assertEquals(DocumentType.VENDOR_INVOICE, head.getDocumentType());
		assertEquals(TestUtilities.TEST_DESCP, head.getDocText());
		assertEquals(2, head.getItemCount());

		ItemEntity[] items = head.getItems();
		for (ItemEntity item : items) {
			if (item.getCDIndicator() == CreditDebitIndicator.CREDIT) {
				assertEquals(TestUtilities.GL_ACCOUNT1, item.getGLAccount()
						.toString());
				assertEquals(TestUtilities.VENDOR, item.getVendor().toString());
				assertEquals(AccountType.VENDOR, item.getAccountType());
				assertEquals(100, (int) item.getAmount());
			} else {
				assertEquals(TestUtilities.GL_ACCOUNT_COST, item.getGLAccount()
						.toString());
				assertEquals(AccountType.GL_ACCOUNT, item.getAccountType());
				assertEquals(100, (int) item.getAmount());
				assertEquals(TestUtilities.BUSINESS_AREA, item
						.getBusinessArea().toString());
			}
		}
	}

}

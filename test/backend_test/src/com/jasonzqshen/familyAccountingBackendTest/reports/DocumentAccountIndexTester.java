package com.jasonzqshen.familyAccountingBackendTest.reports;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import com.jasonzqshen.familyAccountingBackendTest.utils.TestData;
import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.reports.DocumentIndex;
import com.jasonzqshen.familyaccounting.core.reports.DocumentIndexItem;
import com.jasonzqshen.familyaccounting.core.reports.ReportsManagement;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;

public class DocumentAccountIndexTester extends TesterBase {

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		// set root path
		coreDriver.setRootPath(TestUtilities.TEST_ACC_INDEX);
	}

	@Override
	protected void check(CoreDriver coreDriver) throws Exception {
		ReportsManagement rpMgmt = coreDriver.getReportsManagement();

		DocumentIndex index = rpMgmt
				.getDocumentIndex(DocumentIndex.ACCOUNT_INDEX);
		// check count of accounts involved
		assertEquals(4, index.getKeys().size());

		MonthIdentity month07 = new MonthIdentity(2012, 7);
		MonthIdentity month08 = new MonthIdentity(2012, 8);

		// check the cash account
		MasterDataIdentity_GLAccount glAccount2 = new MasterDataIdentity_GLAccount(
				TestData.GL_ACCOUNT_CASH);
		DocumentIndexItem item = index.getIndexItem(glAccount2);
		ArrayList<HeadEntity> entities = item.getEntities();
		assertEquals(2, entities.size());

		entities = item.getEntities(month07, month07);
		assertEquals(2, entities.size());

		entities = item.getEntities(month08, month08);
		assertEquals(0, entities.size());

		// check cash amount
		assertEquals(new CurrencyAmount(-23.45), item.getAmount());
		assertEquals(new CurrencyAmount(-23.45), item.getAmount(month07));
		assertEquals(new CurrencyAmount(0.00), item.getAmount(month08));

		// check cost account
		MasterDataIdentity_GLAccount costAccount = new MasterDataIdentity_GLAccount(
				TestData.GL_ACCOUNT_COST);
		DocumentIndexItem costItem = index.getIndexItem(costAccount);
		entities = costItem.getEntities();
		assertEquals(1, entities.size());

		entities = costItem.getEntities(month07, month07);
		assertEquals(1, entities.size());

		entities = costItem.getEntities(month08, month08);
		assertEquals(0, entities.size());

		// check cost amount
		assertEquals(new CurrencyAmount(123.45), costItem.getAmount());
		assertEquals(new CurrencyAmount(123.45), costItem.getAmount(month07));
		assertEquals(new CurrencyAmount(0.00), costItem.getAmount(month08));
	}

}

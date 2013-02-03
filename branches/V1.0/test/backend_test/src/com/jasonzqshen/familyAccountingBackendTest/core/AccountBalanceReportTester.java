package com.jasonzqshen.familyAccountingBackendTest.core;

import static org.junit.Assert.assertEquals;

import com.jasonzqshen.familyAccountingBackendTest.utils.TestData;
import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceCollection;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceItem;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

public class AccountBalanceReportTester extends TesterBase {

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		// set root path
		coreDriver.setRootPath(TestUtilities.TEST_ACC_BALANCE_REPORT);
	}

	@Override
	protected void check(CoreDriver coreDriver) throws Exception {
		TransactionDataManagement transMgmt = coreDriver
				.getTransDataManagement();
		GLAccountBalanceCollection balCol = transMgmt.getAccBalCol();

		MonthIdentity month07 = new MonthIdentity(2012, 7);
		MonthIdentity month08 = new MonthIdentity(2012, 8);

		MasterDataIdentity_GLAccount glAccount2 = new MasterDataIdentity_GLAccount(
				TestData.GL_ACCOUNT_CASH);
		GLAccountBalanceItem balItem = balCol.getBalanceItem(glAccount2);
		CurrencyAmount amount2 = new CurrencyAmount(-23.45);
		assertEquals(amount2, balItem.getSumAmount());
		assertEquals(amount2, balItem.getAmount(month07));
		assertEquals(new CurrencyAmount(), balItem.getAmount(month08));

		assertEquals(amount2, balCol.getGroupBalance(GLAccountGroup.CASH));
		assertEquals(amount2,
				balCol.getGroupBalance(GLAccountGroup.CASH, month07, month07));
		assertEquals(new CurrencyAmount(),
				balCol.getGroupBalance(GLAccountGroup.CASH, month08, month08));

		// cost
		assertEquals(new CurrencyAmount(123.45),
				balCol.getGroupBalance(GLAccountGroup.COST_PURE));
		assertEquals(new CurrencyAmount(123.45), balCol.getGroupBalance(
				GLAccountGroup.COST_PURE, month07, month07));
		assertEquals(new CurrencyAmount(), balCol.getGroupBalance(
				GLAccountGroup.COST_PURE, month08, month08));

		// revenue
		assertEquals(new CurrencyAmount(-543.21),
				balCol.getGroupBalance(GLAccountGroup.SALARY));
		assertEquals(new CurrencyAmount(-543.21),
				balCol.getGroupBalance(GLAccountGroup.SALARY, month07, month07));
		assertEquals(new CurrencyAmount(),
				balCol.getGroupBalance(GLAccountGroup.SALARY, month08, month08));

	}
}

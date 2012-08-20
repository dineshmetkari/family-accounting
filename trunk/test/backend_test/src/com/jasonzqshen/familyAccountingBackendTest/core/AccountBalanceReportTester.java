package com.jasonzqshen.familyAccountingBackendTest.core;

import static org.junit.Assert.assertEquals;

import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceCollection;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceItem;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

public class AccountBalanceReportTester extends TesterBase {

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		// set root path
		coreDriver.setRootPath(TestUtilities.TEST_ROOT_FOLDER);
	}

	@Override
	protected void check(CoreDriver coreDriver) throws Exception {
		TransactionDataManagement transMgmt = coreDriver
				.getTransDataManagement();
		GLAccountBalanceCollection balCol = transMgmt.getAccBalCol();

		MasterDataIdentity_GLAccount glAccount2 = new MasterDataIdentity_GLAccount(
				TestUtilities.GL_ACCOUNT2);
		GLAccountBalanceItem balItem = balCol.getBalanceItem(glAccount2);
		CurrencyAmount amount2 = new CurrencyAmount(419.76);
		assertEquals(amount2, balItem.getSumAmount());
		assertEquals(amount2, balItem.getAmount(coreDriver.getStartMonthId()));
		assertEquals(new CurrencyAmount(),
				balItem.getAmount(coreDriver.getCurMonthId()));

		assertEquals(amount2, balCol.getGroupBalance(GLAccountGroup.CASH));
		assertEquals(
				amount2,
				balCol.getGroupBalance(GLAccountGroup.CASH,
						coreDriver.getStartMonthId(),
						coreDriver.getStartMonthId()));
		assertEquals(
				new CurrencyAmount(),
				balCol.getGroupBalance(GLAccountGroup.CASH,
						coreDriver.getCurMonthId(),
						coreDriver.getCurMonthId()));
	}
}

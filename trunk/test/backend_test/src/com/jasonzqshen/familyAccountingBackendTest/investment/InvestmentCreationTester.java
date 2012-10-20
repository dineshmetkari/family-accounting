package com.jasonzqshen.familyAccountingBackendTest.investment;

import java.util.Calendar;
import java.util.Date;

import com.jasonzqshen.familyAccountingBackendTest.utils.MasterDataCreater;
import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.investment.InvestmentAccount;
import com.jasonzqshen.familyaccounting.core.investment.InvestmentItem;
import com.jasonzqshen.familyaccounting.core.investment.InvestmentManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;

public class InvestmentCreationTester extends TesterBase {
	private InvestmentManagement _investMgmt;

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		TestUtilities.establishFolder2012_07(
				TestUtilities.TEST_ROOT_CREATE_INVESTMENT, coreDriver);

		_investMgmt = new InvestmentManagement(coreDriver);
		// coreDriver.setRootPath(TestUtilities.TEST_ROOT_CREATE_INVESTMENT);
		_investMgmt.initialize();

		MasterDataCreater.createMasterData(coreDriver);

		// create investment account
		MasterDataIdentity_GLAccount investAccount = new MasterDataIdentity_GLAccount(
				"1010100001");
		MasterDataIdentity_GLAccount revAccount = new MasterDataIdentity_GLAccount(
				"4000100001");
		InvestmentAccount investAcc = _investMgmt.createInvestAccount(
				investAccount, revAccount, "deposite account in ICBC");

		// create investment item
		MasterDataIdentity_GLAccount srcAccount = new MasterDataIdentity_GLAccount(
				"1000100001");
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2012);
		calendar.set(Calendar.MONTH, 6);
		calendar.set(Calendar.DATE, 2);
		Date startDate = calendar.getTime();
		calendar.set(Calendar.DATE, 30);
		Date dueDate = calendar.getTime();
		InvestmentItem investItem = investAcc.createInvestment(startDate,
				dueDate, srcAccount, new CurrencyAmount(1000.0));

		// commit
		investItem.commit(new CurrencyAmount(1030.0));

		startDate = dueDate;
		calendar.set(Calendar.MONTH, 7);
		dueDate = calendar.getTime();
		investAcc.createInvestment(startDate, dueDate, srcAccount,
				new CurrencyAmount(1030.0));

	}

	@Override
	protected void check(CoreDriver coreDriver) throws Exception {
		InvesetmentLoadingTester.checkInvestment(coreDriver, _investMgmt);

		// reload
		coreDriver.restart();
		_investMgmt.clear();
		_investMgmt.initialize();
		InvesetmentLoadingTester.checkInvestment(coreDriver, _investMgmt);
	}

}

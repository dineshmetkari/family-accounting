package com.jasonzqshen.familyAccountingBackendTest.core;

import com.jasonzqshen.familyAccountingBackendTest.utils.*;
import com.jasonzqshen.familyaccounting.core.CoreDriver;

public class InitializeWithDataTester extends TesterBase {

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		// set root path
		coreDriver.setRootPath(TestUtilities.TEST_INIT);
	}

	@Override
	protected void check(CoreDriver coreDriver) throws Exception {
		MasterDataChecker.checkMasterData(coreDriver);
		TransactionDataChecker.checkTransactionData(coreDriver);

	}

}

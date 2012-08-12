package com.jasonzqshen.familyAccountingBackendTest.core;

import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyaccounting.core.CoreDriver;

public class MasterDataCreationTester extends TesterBase {

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		// TODO Auto-generated method stub
		TestUtilities.clearTestingRootFolder();

		TestUtilities.establishMasterData(coreDriver);

		// store
		coreDriver.getMasterDataManagement().store();
	}

}

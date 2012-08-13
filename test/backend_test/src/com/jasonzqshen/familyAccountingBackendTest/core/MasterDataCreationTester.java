package com.jasonzqshen.familyAccountingBackendTest.core;

import static org.junit.Assert.assertEquals;

import com.jasonzqshen.familyAccountingBackendTest.utils.MasterDataCreater;
import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyaccounting.core.CoreDriver;

public class MasterDataCreationTester extends TesterBase {

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		// TODO Auto-generated method stub
		TestUtilities
				.clearTestingRootFolder(TestUtilities.TEST_ROOT_MASTER_CREATION);

		coreDriver.setRootPath(TestUtilities.TEST_ROOT_MASTER_CREATION);

		assertEquals(true, coreDriver.isInitialized());
		MasterDataCreater.createMasterData(coreDriver);

		// store
		coreDriver.getMasterDataManagement().store();
	}

}

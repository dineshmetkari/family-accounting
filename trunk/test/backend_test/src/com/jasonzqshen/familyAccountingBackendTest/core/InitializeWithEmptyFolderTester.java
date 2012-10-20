package com.jasonzqshen.familyAccountingBackendTest.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataFactoryBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;

public class InitializeWithEmptyFolderTester extends TesterBase {

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		TestUtilities
				.clearTestingRootFolder(TestUtilities.TEST_ROOT_EMPTY_INIT);
		coreDriver.setRootPath(TestUtilities.TEST_ROOT_EMPTY_INIT);

	}

	private void checkCore(CoreDriver coreDriver) {
		assertEquals(true, coreDriver.isInitialized());

		MasterDataManagement masterData = coreDriver.getMasterDataManagement();
		for (MasterDataType type : MasterDataType.values()) {
			MasterDataFactoryBase factory = masterData
					.getMasterDataFactory(type);
			assertEquals(0, factory.getAllEntities().length);
		}

		TransactionDataManagement tranData = coreDriver
				.getTransDataManagement();
		assertTrue(null != tranData);
		MonthIdentity[] monthIds = tranData.getAllMonthIds();
		assertEquals(1, monthIds.length);
	}

	@Override
	protected void check(CoreDriver coreDriver) throws Exception {
		checkCore(coreDriver);
		coreDriver.restart();
		checkCore(coreDriver);
	}

}

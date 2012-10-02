package com.jasonzqshen.familyAccountingBackendTest.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jasonzqshen.familyAccountingBackendTest.utils.TestData;
import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.masterdata.VendorMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.VendorMasterDataFactory;

/**
 * Test special symbol for XML
 * 
 * @author jasonzqshen@gmail.com
 * 
 */
public class SpecialSymbolTester extends TesterBase {
	private static final String DESCP = "&'/><\\";

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		TestUtilities.clearTestingRootFolder(TestUtilities.TEST_SPECIAL_SYMBOL);

		coreDriver.setRootPath(TestUtilities.TEST_SPECIAL_SYMBOL);

		assertEquals(true, coreDriver.isInitialized());
		/**
		 * check the factory is initialized, and the factory with no master data
		 * entities
		 */
		MasterDataManagement masterDataManagement = coreDriver
				.getMasterDataManagement();
		// get vendor factory
		VendorMasterDataFactory vendorFactory = (VendorMasterDataFactory) masterDataManagement
				.getMasterDataFactory(MasterDataType.VENDOR);
		assertEquals(0, vendorFactory.getMasterDataCount());
		/** add master data entities */
		VendorMasterData vendor = (VendorMasterData) vendorFactory
				.createNewMasterDataBase(new MasterDataIdentity(
						TestData.VENDOR_BUS), DESCP);
		assertTrue(vendor != null);
	}

	@Override
	protected void check(CoreDriver coreDriver) throws Exception {
		checkMasterData(coreDriver);
		// store
		coreDriver.getMasterDataManagement().store();
		coreDriver.restart();

		checkMasterData(coreDriver);
	}

	/**
	 * check master data
	 * 
	 * @param coreDriver
	 */
	private void checkMasterData(CoreDriver coreDriver) {
		assertEquals(true, coreDriver.isInitialized());
		
		MasterDataManagement masterDataManagement = coreDriver
				.getMasterDataManagement();
		
		MasterDataBase[] datas;

		// check vendor data
		VendorMasterDataFactory vendorFactory = (VendorMasterDataFactory) masterDataManagement
				.getMasterDataFactory(MasterDataType.VENDOR);
		assertEquals(1, vendorFactory.getMasterDataCount());
		datas = vendorFactory.getAllEntities();
		assertEquals(TestData.VENDOR_BUS, datas[0].getIdentity().toString());
		assertEquals(DESCP, datas[0].getDescp());
	}

}

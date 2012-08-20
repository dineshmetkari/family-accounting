package com.jasonzqshen.familyAccountingBackendTest.reports;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.reports.DocumentIndex;
import com.jasonzqshen.familyaccounting.core.reports.DocumentIndexItem;
import com.jasonzqshen.familyaccounting.core.reports.ReportsManagement;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;

public class DocumentAccountIndexTester extends TesterBase {
	private ReportsManagement _reportsManagement;

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		_reportsManagement = new ReportsManagement(coreDriver);

		// set root path
		coreDriver.setRootPath(TestUtilities.TEST_ROOT_FOLDER);
	}

	@Override
	protected void check(CoreDriver coreDriver) throws Exception {
		DocumentIndex index = _reportsManagement
				.getDocumentIndex(DocumentIndex.ACCOUNT_INDEX);
		assertEquals(4, index.getKeys().size());
		MasterDataIdentity_GLAccount glAccount2 = new MasterDataIdentity_GLAccount(
				TestUtilities.GL_ACCOUNT2);
		DocumentIndexItem item = index.getIndexItem(glAccount2);
		ArrayList<HeadEntity> entities = item.getEntities();
		assertEquals(4, entities.size());

		entities = item.getEntities(coreDriver.getStartMonthId(),
				coreDriver.getCurMonthId());
		assertEquals(4, entities.size());
	}

}

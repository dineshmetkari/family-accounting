package com.jasonzqshen.familyAccountingBackendTest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.reports.ReportsManagement;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;

public class ReportTestCase {
	@Test
	public void testGLAccountBalanceReport() throws Exception {
		try {
			ArrayList<CoreMessage> messages = new ArrayList<CoreMessage>();
			CoreDriver coreDriver = CoreDriver.getInstance();
			coreDriver.clear();

			// set root path
			coreDriver.setRootPath(TestUtilities.TEST_ROOT_FOLDER);
			coreDriver.setStartMonthID(new MonthIdentity(2012, 7));

			coreDriver.init(messages);

			// check
			MasterDataIdentity_GLAccount account1 = new MasterDataIdentity_GLAccount(
					TestUtilities.GL_ACCOUNT1.toCharArray());
			MasterDataIdentity_GLAccount account2 = new MasterDataIdentity_GLAccount(
					TestUtilities.GL_ACCOUNT2.toCharArray());
			ReportsManagement reports = coreDriver.getReportsManagement();
			assertEquals(TestUtilities.GL_IDS.length, reports.getGLAccounts()
					.size());

			assertEquals(20000,
					(int) (reports.getGLAccountBalance(account1) * 100));
			assertEquals(-20000,
					(int) (reports.getGLAccountBalance(account2) * 100));

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TestUtilities.saveLogFile("testMasterDataInit.txt",
					CoreDriver.getInstance());
		}
	}
}

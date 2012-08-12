package com.jasonzqshen.familyAccountingBackendTest.utils;

import org.junit.Test;

import com.jasonzqshen.familyaccounting.core.CoreDriver;

public abstract class TesterBase {
	@Test
	public void launchTest() throws Exception {
		CoreDriver coreDriver = new CoreDriver();
		try {
			doTest(coreDriver);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TestUtilities.saveLogFile(this.getClass().getSimpleName() + ".txt",
					coreDriver);
		}
	}

	protected abstract void doTest(CoreDriver coreDriver) throws Exception;
}

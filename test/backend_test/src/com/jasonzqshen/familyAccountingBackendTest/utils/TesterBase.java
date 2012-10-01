package com.jasonzqshen.familyAccountingBackendTest.utils;


import org.junit.Test;

import com.jasonzqshen.familyaccounting.core.CoreDriver;

public abstract class TesterBase {
	@Test
	public void launchTest() throws Exception {
		CoreDriver coreDriver = new CoreDriver();
		coreDriver.setFlushLog(true);
		try {
			doTest(coreDriver);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
		
		check(coreDriver);
	}

	protected abstract void doTest(CoreDriver coreDriver) throws Exception;
	protected abstract void check(CoreDriver coreDriver) throws Exception;
	
}

package com.jasonzqshen.familyAccountingBackendTest.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.utils.DebugInformation;

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
		}
		
		check(coreDriver);
	}

	protected abstract void doTest(CoreDriver coreDriver) throws Exception;
	protected abstract void check(CoreDriver coreDriver) throws Exception;
	
}

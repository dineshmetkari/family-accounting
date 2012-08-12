package com.jasonzqshen.familyAccountingBackendTest.basic;

import static org.junit.Assert.assertEquals;

import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.transaction.DocumentIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.DocumentNumber;

public class DocumentIdentityTester extends TesterBase {

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		DocumentNumber docNum = new DocumentNumber(
				TestUtilities.TEST_DOC_NUM.toCharArray());
		DocumentIdentity id = new DocumentIdentity(docNum, 2012, 07);
		String idStr = id.toString();
		assertEquals(TestUtilities.TEST_DOC_ID, idStr);
		DocumentIdentity newId = DocumentIdentity.parse(idStr);
		assertEquals(newId, id);

	}

}

package com.jasonzqshen.familyAccountingBackendTest.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.masterdata.BankAccountNumber;

public class BankAccountNumberTester extends TesterBase {

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		// test length
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i <= 16; ++i) {
			builder.append('0');
			try {
				char[] testCase = builder.toString().toCharArray();
				BankAccountNumber test = new BankAccountNumber(testCase);
				test.toString();
			} catch (Exception e) {
				fail(String.format("Length %d: %s", i, e.toString()));
			}
		}

		// test number
		try {
			char[] testCase = "123456789".toCharArray();
			BankAccountNumber test = new BankAccountNumber(testCase);
			assertEquals(test.toString(), "0000000123456789");
		} catch (Exception e) {
			fail(e.toString());
		}

		// test number
		try {
			char[] testCase = "0000000000000000".toCharArray();
			BankAccountNumber test = new BankAccountNumber(testCase);
			assertEquals(test.toString(), "0000000000000000");
		} catch (Exception e) {
			fail(e.toString());
		}

		// test equals
		try {
			char[] testCase = "123".toCharArray();
			BankAccountNumber test1 = new BankAccountNumber(testCase);
			BankAccountNumber test2 = new BankAccountNumber(testCase);
			assertEquals(test1, test2);
			assertEquals(test1.hashCode(), test2.hashCode());
		} catch (Exception e) {
			fail(e.toString());
		}

		// zero length
		try {
			char[] zeroTest = "".toCharArray();
			BankAccountNumber test = new BankAccountNumber(zeroTest);
			test.toString();
			fail("IdentityNoData should occur when length is zero");
		} catch (IdentityNoData e) {
			// pass
		} catch (Exception e) {
			fail("IdentityNoData should occur when length is zero, but it is other exception");
		}

		// format error
		try {
			char[] testCase = "a?".toCharArray();
			BankAccountNumber test = new BankAccountNumber(testCase);
			test.toString();
			fail("IdentityNoData should occur when there is no data");
		} catch (IdentityInvalidChar e) {
			// pass
		} catch (Exception e) {
			fail("IdentityInvalidChar should occur when length is zero, but it is other exception");
		}

		// too long
		try {
			char[] testCase = "12345678901234567".toCharArray();
			BankAccountNumber test = new BankAccountNumber(testCase);
			test.toString();
			fail("IdentityNoData should occur when there is no data");
		} catch (IdentityTooLong e) {
			// pass
		} catch (Exception e) {
			fail("IdentityInvalidChar should occur when length is zero, but it is other exception");
		}

	}

	@Override
	protected void check(CoreDriver coreDriver) throws Exception {
		// TODO Auto-generated method stub
		
	}

}

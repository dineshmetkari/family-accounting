package com.jasonzqshen.familyAccountingBackendTest.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.jasonzqshen.familyAccountingBackendTest.utils.*;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;

public class MasterDataIdentityTester extends TesterBase {
	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		// test length
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i <= 10; ++i) {
			builder.append('A');
			try {
				char[] testCase = builder.toString().toCharArray();
				MasterDataIdentity test = new MasterDataIdentity(testCase);
				test.toString();
			} catch (Exception e) {
				fail(String.format("Length %d: %s", i, e.toString()));
			}
		}

		// test low case
		try {
			char[] testCase = "abcdefg".toCharArray();
			MasterDataIdentity test = new MasterDataIdentity(testCase);
			assertEquals(test.toString(), "000ABCDEFG");
		} catch (Exception e) {
			fail(e.toString());
		}

		// test number
		try {
			char[] testCase = "123456789".toCharArray();
			MasterDataIdentity test = new MasterDataIdentity(testCase);
			assertEquals(test.toString(), "0123456789");
		} catch (Exception e) {
			fail(e.toString());
		}

		// test '_'
		try {
			char[] testCase = "_123456789".toCharArray();
			MasterDataIdentity test = new MasterDataIdentity(testCase);
			assertEquals(test.toString(), "_123456789");
		} catch (Exception e) {
			fail(e.toString());
		}

		// test equals
		try {
			char[] testCase = "123".toCharArray();
			MasterDataIdentity test1 = new MasterDataIdentity(testCase);
			MasterDataIdentity test2 = new MasterDataIdentity(testCase);
			assertEquals(test1, test2);
			assertEquals(test1.hashCode(), test2.hashCode());
		} catch (Exception e) {
			fail(e.toString());
		}

		// zero length
		try {
			char[] zeroTest = "".toCharArray();
			MasterDataIdentity test = new MasterDataIdentity(zeroTest);
			test.toString();
			fail("IdentityNoData should occur when length is zero");
		} catch (IdentityNoData e) {
			// pass
		} catch (Exception e) {
			fail("IdentityNoData should occur when length is zero, but it is other exception");
		}

		// no data
		try {
			char[] noDataTest = "00".toCharArray();
			MasterDataIdentity test = new MasterDataIdentity(noDataTest);
			test.toString();
			fail("IdentityNoData should occur when there is no data");
		} catch (IdentityNoData e) {
			// pass
		} catch (Exception e) {
			fail("IdentityNoData should occur when length is zero, but it is other exception");
		}

		// format error
		try {
			char[] testCase = "a?".toCharArray();
			MasterDataIdentity test = new MasterDataIdentity(testCase);
			test.toString();
			fail("IdentityNoData should occur when there is no data");
		} catch (IdentityInvalidChar e) {
			// pass
		} catch (Exception e) {
			fail("IdentityInvalidChar should occur when length is zero, but it is other exception");
		}

		// too long
		try {
			char[] testCase = "12345678901".toCharArray();
			MasterDataIdentity test = new MasterDataIdentity(testCase);
			test.toString();
			fail("IdentityNoData should occur when there is no data");
		} catch (IdentityTooLong e) {
			// pass
		} catch (Exception e) {
			fail("IdentityInvalidChar should occur when length is zero, but it is other exception");
		}

		// identity for gl account
		try {
			char[] testCase = "113100".toCharArray();
			MasterDataIdentity_GLAccount test = new MasterDataIdentity_GLAccount(
					testCase);
			assertEquals(test.toString(), "0000113100");
		} catch (Exception e) {
			fail(e.toString());
		}

		// identity for gl account
		// format error
		try {
			char[] testCase = "abcd".toCharArray();
			MasterDataIdentity_GLAccount test = new MasterDataIdentity_GLAccount(
					testCase);
			test.toString();
			fail("IdentityNoData should occur when there is no data");
		} catch (IdentityInvalidChar e) {
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

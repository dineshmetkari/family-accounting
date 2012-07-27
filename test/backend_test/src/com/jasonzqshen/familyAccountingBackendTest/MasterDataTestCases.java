package com.jasonzqshen.familyAccountingBackendTest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;

public class MasterDataTestCases {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * test identity
	 */
	@Test
	public void testMasterDataIdentity() {
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

	/**
	 * test G/L account master data
	 */
	@Test
	public void testGLAccount() {
		try {
			MasterDataIdentity_GLAccount id1 = new MasterDataIdentity_GLAccount(
					"113100".toCharArray());
			MasterDataIdentity_GLAccount id1 = new MasterDataIdentity_GLAccount(
					"113101".toCharArray());
		} catch (Exception e) {
			fail(e.toString());
		}
	}
}

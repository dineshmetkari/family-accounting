package com.jasonzqshen.familyAccountingBackendTest.utils;

import static org.junit.Assert.*;

import java.util.Calendar;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthLedger;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.AccountType;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;

public class TransactionDataChecker {
	/**
	 * check transaction data
	 * 
	 * @param coreDriver
	 */
	public static void checkTransactionData(CoreDriver coreDriver) {
		TransactionDataManagement transManagement = coreDriver
				.getTransDataManagement();
		MonthIdentity[] monthIds = transManagement.getAllMonthIds();
		assertEquals(2, monthIds.length);

		MonthLedger ledger07 = transManagement.getLedger(monthIds[0]);
		MonthLedger ledger08 = transManagement.getLedger(monthIds[1]);
		assertEquals(true, ledger07.isClosed());
		assertEquals(false, ledger08.isClosed());
		assertTrue(transManagement.getCurrentLedger() == ledger08);

		checkLedger2012_07(ledger07.getEntities());
		checkLedger2012_08(ledger08.getEntities());
	}

	/**
	 * check ledger 2012_07
	 * 
	 * @param collection
	 */
	public static void checkLedger2012_07(HeadEntity[] docs) {
		assertEquals(3, docs.length);

		// check values
		for (int i = 0; i < docs.length; ++i) {
			HeadEntity head = docs[i];
			// posting date
			Calendar cal = Calendar.getInstance();
			cal.setTime(head.getPostingDate());
			assertEquals(2012, cal.get(Calendar.YEAR));
			assertEquals(7, cal.get(Calendar.MONTH) + 1);
			assertEquals(2, cal.get(Calendar.DATE));

			// document type
			if (i == 0) {
				assertEquals(DocumentType.VENDOR_INVOICE,
						head.getDocumentType());
			} else if (i == 1) {
				assertEquals(DocumentType.CUSTOMER_INVOICE,
						head.getDocumentType());
			} else {
				assertEquals(DocumentType.GL, head.getDocumentType());
			}

			// is reversed
			assertEquals(false, head.IsReversed());
			assertEquals(null, head.getReference());

			if (i == 2) {
				checkClosingDoc(head);
			} else {
				// text
				assertEquals(TestUtilities.TEST_DESCP, head.getDocText());

				// items
				ItemEntity[] items = head.getItems();
				assertEquals(2, items.length);
				if (i == 0) {
					checkCostItem(items[0]);
					checkVendorItem(items[1]);
				} else {
					checkProfitItem(items[0]);
					checkCustomerItem(items[1]);
				}
			}

		}
	}

	/**
	 * check ledger 2012_07
	 * 
	 * @param collection
	 */
	public static void checkLedger2012_08(HeadEntity[] docs) {
		assertEquals(2, docs.length);

		// check values
		for (int i = 0; i < docs.length; ++i) {
			HeadEntity head = docs[i];
			// posting date
			Calendar cal = Calendar.getInstance();
			cal.setTime(head.getPostingDate());
			assertEquals(2012, cal.get(Calendar.YEAR));
			assertEquals(8, cal.get(Calendar.MONTH) + 1);
			assertEquals(2, cal.get(Calendar.DATE));

			// document type
			assertEquals(DocumentType.VENDOR_INVOICE, head.getDocumentType());

			// is reversed
			assertEquals(true, head.IsReversed());
			assertEquals(docs[1].getDocIdentity(), head.getReference());

			if (i == 2) {
				checkClosingDoc(head);
			} else {
				// text
				assertEquals(TestUtilities.TEST_DESCP, head.getDocText());

				// items
				ItemEntity[] items = head.getItems();
				assertEquals(2, items.length);
				if (i == 0) {
					checkCostItem(items[0]);
					checkVendorItem(items[1]);
				} else {
					checkCostItemRev(items[0]);
					checkVendorItemRev(items[1]);
				}
			}

		}
	}

	/**
	 * check closing document
	 * 
	 * @param closingDoc
	 */
	private static void checkClosingDoc(HeadEntity closingDoc) {
		// text
		assertEquals(MonthLedger.CLOSING_DOC_TAG, closingDoc.getDocText());
		// items
		ItemEntity[] items = closingDoc.getItems();
		assertEquals(3, items.length);

		checkProfitItemRev(items[0]);
		ItemEntity item = items[1];
		assertEquals(TestUtilities.GL_ACCOUNT_COST, item.getGLAccount()
				.toString());
		assertEquals(null, item.getVendor());
		assertEquals(null, item.getCustomer());
		assertEquals(AccountType.GL_ACCOUNT, item.getAccountType());
		assertEquals(TestUtilities.TEST_AMOUNT1, item.getAmount().toString());
		assertEquals(CreditDebitIndicator.CREDIT, item.getCDIndicator());
		
		
		item = items[2];
		assertEquals(TestUtilities.GL_ACCOUNT_ENQUITY, item.getGLAccount()
				.toString());
		assertEquals(null, item.getVendor());
		assertEquals(null, item.getCustomer());
		assertEquals(AccountType.GL_ACCOUNT, item.getAccountType());
		assertEquals(TestUtilities.TEST_AMOUNT3, item.getAmount().toString());
		assertEquals(CreditDebitIndicator.CREDIT, item.getCDIndicator());
	}

	/**
	 * check cost item
	 * 
	 * @param costItem
	 */
	private static void checkCostItem(ItemEntity item) {
		assertEquals(TestUtilities.GL_ACCOUNT_COST, item.getGLAccount()
				.toString());
		assertEquals(null, item.getVendor());
		assertEquals(null, item.getCustomer());
		assertEquals(AccountType.GL_ACCOUNT, item.getAccountType());
		assertEquals(TestUtilities.TEST_AMOUNT1, item.getAmount().toString());
		assertEquals(CreditDebitIndicator.DEBIT, item.getCDIndicator());

		assertEquals(TestUtilities.BUSINESS_AREA, item.getBusinessArea()
				.toString());
	}

	/**
	 * check vendor item
	 * 
	 * @param costItem
	 */
	private static void checkVendorItem(ItemEntity item) {
		assertEquals(TestUtilities.GL_ACCOUNT2, item.getGLAccount().toString());
		assertEquals(TestUtilities.VENDOR, item.getVendor().toString());
		assertEquals(null, item.getCustomer());
		assertEquals(AccountType.VENDOR, item.getAccountType());
		assertEquals(TestUtilities.TEST_AMOUNT1, item.getAmount().toString());
		assertEquals(CreditDebitIndicator.CREDIT, item.getCDIndicator());

		assertEquals(null, item.getBusinessArea());
	}

	/**
	 * check cost item
	 * 
	 * @param costItem
	 */
	private static void checkCostItemRev(ItemEntity item) {
		assertEquals(TestUtilities.GL_ACCOUNT_COST, item.getGLAccount()
				.toString());
		assertEquals(null, item.getVendor());
		assertEquals(null, item.getCustomer());
		assertEquals(AccountType.GL_ACCOUNT, item.getAccountType());
		assertEquals(TestUtilities.TEST_AMOUNT1, item.getAmount().toString());
		assertEquals(CreditDebitIndicator.CREDIT, item.getCDIndicator());

		assertEquals(TestUtilities.BUSINESS_AREA, item.getBusinessArea()
				.toString());
	}

	/**
	 * check vendor item
	 * 
	 * @param costItem
	 */
	private static void checkVendorItemRev(ItemEntity item) {
		assertEquals(TestUtilities.GL_ACCOUNT2, item.getGLAccount().toString());
		assertEquals(TestUtilities.VENDOR, item.getVendor().toString());
		assertEquals(null, item.getCustomer());
		assertEquals(AccountType.VENDOR, item.getAccountType());
		assertEquals(TestUtilities.TEST_AMOUNT1, item.getAmount().toString());
		assertEquals(CreditDebitIndicator.DEBIT, item.getCDIndicator());

		assertEquals(null, item.getBusinessArea());
	}

	/**
	 * check profit item
	 * 
	 * @param costItem
	 */
	private static void checkProfitItem(ItemEntity item) {
		assertEquals(TestUtilities.GL_ACCOUNT_PROFIT, item.getGLAccount()
				.toString());
		assertEquals(null, item.getVendor());
		assertEquals(null, item.getCustomer());
		assertEquals(AccountType.GL_ACCOUNT, item.getAccountType());
		assertEquals(TestUtilities.TEST_AMOUNT2, item.getAmount().toString());
		assertEquals(CreditDebitIndicator.CREDIT, item.getCDIndicator());

		assertEquals(null, item.getBusinessArea());
	}

	/**
	 * check profit item
	 * 
	 * @param costItem
	 */
	private static void checkProfitItemRev(ItemEntity item) {
		assertEquals(TestUtilities.GL_ACCOUNT_PROFIT, item.getGLAccount()
				.toString());
		assertEquals(null, item.getVendor());
		assertEquals(null, item.getCustomer());
		assertEquals(AccountType.GL_ACCOUNT, item.getAccountType());
		assertEquals(TestUtilities.TEST_AMOUNT2, item.getAmount().toString());
		assertEquals(CreditDebitIndicator.DEBIT, item.getCDIndicator());

		assertEquals(null, item.getBusinessArea());
	}

	/**
	 * check customer item
	 * 
	 * @param costItem
	 */
	private static void checkCustomerItem(ItemEntity item) {
		assertEquals(TestUtilities.GL_ACCOUNT2, item.getGLAccount().toString());
		assertEquals(TestUtilities.CUSTOMER, item.getCustomer().toString());
		assertEquals(null, item.getVendor());
		assertEquals(AccountType.CUSTOMER, item.getAccountType());
		assertEquals(TestUtilities.TEST_AMOUNT2, item.getAmount().toString());
		assertEquals(CreditDebitIndicator.DEBIT, item.getCDIndicator());

		assertEquals(null, item.getBusinessArea());
	}
}

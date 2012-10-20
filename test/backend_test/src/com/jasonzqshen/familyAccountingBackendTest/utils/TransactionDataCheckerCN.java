package com.jasonzqshen.familyAccountingBackendTest.utils;

import static org.junit.Assert.*;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthLedger;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.AccountType;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;

public class TransactionDataCheckerCN {
	/**
	 * check transaction data
	 * 
	 * @param coreDriver
	 */
	public static void checkTransactionData(CoreDriver coreDriver) {
		TransactionDataManagement transManagement = coreDriver
				.getTransDataManagement();
		MonthIdentity[] monthIds = transManagement.getAllMonthIds();
		// assertEquals(2, monthIds.length);

		MonthLedger ledger07 = transManagement.getLedger(monthIds[0]);
		MonthLedger ledger08 = transManagement.getLedger(monthIds[1]);

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

		checkVendorDoc(docs[0]);
		checkCustomerDoc(docs[1]);
		checkGLDoc(docs[2]);
		// checkClosingDoc(docs[3]);
	}

	/**
	 * check vendor document
	 * 
	 * @param vendorDoc
	 */
	private static void checkVendorDoc(HeadEntity vendorDoc) {
		// check document number
		assertEquals(TestData.DOC_NUM1, vendorDoc.getDocumentNumber()
				.toString());
		// check posting date
		assertEquals(TestData.DATE_2012_07,
				TestData.DATE_FORMAT.format(vendorDoc.getPostingDate()));
		MonthIdentity monthId = vendorDoc.getMonthId();
		assertEquals(TestData.YEAR, monthId._fiscalYear);
		assertEquals(TestData.MONTH_07, monthId._fiscalMonth);
		// check document type
		assertEquals(DocumentType.VENDOR_INVOICE, vendorDoc.getDocumentType());
		// check text
		assertEquals(TestData.TEXT_VENDOR_DOC_CN, vendorDoc.getDocText());
		// check is reversed
		assertEquals(false, vendorDoc.IsReversed());
		assertEquals(null, vendorDoc.getReference());

		assertEquals(2, vendorDoc.getItemCount());
		ItemEntity[] items = vendorDoc.getItems();
		// check the vendor item
		assertEquals(0, items[0].getLineNum());
		assertEquals(AccountType.VENDOR, items[0].getAccountType());
		assertEquals(CreditDebitIndicator.CREDIT, items[0].getCDIndicator());
		assertEquals(TestData.AMOUNT_VENDOR, items[0].getAmount());
		assertEquals(null, items[0].getCustomer());
		assertEquals(TestData.VENDOR_BUS, items[0].getVendor().toString());
		assertEquals(TestData.GL_ACCOUNT_CASH, items[0].getGLAccount()
				.toString());
		assertEquals(null, items[0].getBusinessArea());

		// check cost item
		assertEquals(1, items[1].getLineNum());
		assertEquals(AccountType.GL_ACCOUNT, items[1].getAccountType());
		assertEquals(CreditDebitIndicator.DEBIT, items[1].getCDIndicator());
		assertEquals(TestData.AMOUNT_VENDOR, items[1].getAmount());
		assertEquals(null, items[1].getCustomer());
		assertEquals(null, items[1].getVendor());
		assertEquals(TestData.GL_ACCOUNT_COST, items[1].getGLAccount()
				.toString());
		assertEquals(TestData.BUSINESS_AREA_WORK, items[1].getBusinessArea()
				.toString());
	}

	/**
	 * check customer document
	 * 
	 * @param customerDoc
	 */
	private static void checkCustomerDoc(HeadEntity customerDoc) {
		// check document number
		assertEquals(TestData.DOC_NUM2, customerDoc.getDocumentNumber()
				.toString());
		// check posting date
		assertEquals(TestData.DATE_2012_07,
				TestData.DATE_FORMAT.format(customerDoc.getPostingDate()));
		MonthIdentity monthId = customerDoc.getMonthId();
		assertEquals(TestData.YEAR, monthId._fiscalYear);
		assertEquals(TestData.MONTH_07, monthId._fiscalMonth);
		// check document type
		assertEquals(DocumentType.CUSTOMER_INVOICE,
				customerDoc.getDocumentType());
		// check text
		assertEquals(TestData.TEXT_CUSTOMER_DOC_CN, customerDoc.getDocText());
		// check is reversed
		assertEquals(false, customerDoc.IsReversed());
		assertEquals(null, customerDoc.getReference());

		assertEquals(2, customerDoc.getItemCount());
		ItemEntity[] items = customerDoc.getItems();
		// check revenue item
		assertEquals(0, items[0].getLineNum());
		assertEquals(AccountType.GL_ACCOUNT, items[0].getAccountType());
		assertEquals(CreditDebitIndicator.CREDIT, items[0].getCDIndicator());
		assertEquals(TestData.AMOUNT_CUSTOMER, items[0].getAmount());
		assertEquals(null, items[0].getCustomer());
		assertEquals(null, items[0].getVendor());
		assertEquals(TestData.GL_ACCOUNT_REV, items[0].getGLAccount()
				.toString());
		assertEquals(null, items[0].getBusinessArea());

		// check customer item
		assertEquals(1, items[1].getLineNum());
		assertEquals(AccountType.CUSTOMER, items[1].getAccountType());
		assertEquals(CreditDebitIndicator.DEBIT, items[1].getCDIndicator());
		assertEquals(TestData.AMOUNT_CUSTOMER, items[1].getAmount());
		assertEquals(TestData.CUSTOMER1, items[1].getCustomer().toString());
		assertEquals(null, items[1].getVendor());
		assertEquals(TestData.GL_ACCOUNT_BANK, items[1].getGLAccount()
				.toString());
		assertEquals(null, items[1].getBusinessArea());
	}

	/**
	 * check customer document
	 * 
	 * @param customerDoc
	 */
	private static void checkGLDoc(HeadEntity glDoc) {
		// check document number
		assertEquals(TestData.DOC_NUM3, glDoc.getDocumentNumber().toString());
		// check posting date
		assertEquals(TestData.DATE_2012_07,
				TestData.DATE_FORMAT.format(glDoc.getPostingDate()));
		MonthIdentity monthId = glDoc.getMonthId();
		assertEquals(TestData.YEAR, monthId._fiscalYear);
		assertEquals(TestData.MONTH_07, monthId._fiscalMonth);
		// check document type
		assertEquals(DocumentType.GL, glDoc.getDocumentType());
		// check text
		assertEquals(TestData.TEXT_GL_DOC_CN, glDoc.getDocText());
		// check is reversed
		assertEquals(false, glDoc.IsReversed());
		assertEquals(null, glDoc.getReference());

		assertEquals(2, glDoc.getItemCount());
		ItemEntity[] items = glDoc.getItems();
		// check the source item
		assertEquals(0, items[0].getLineNum());
		assertEquals(AccountType.GL_ACCOUNT, items[0].getAccountType());
		assertEquals(CreditDebitIndicator.CREDIT, items[0].getCDIndicator());
		assertEquals(TestData.AMOUNT_GL, items[0].getAmount());
		assertEquals(null, items[0].getCustomer());
		assertEquals(null, items[0].getVendor());
		assertEquals(TestData.GL_ACCOUNT_BANK, items[0].getGLAccount()
				.toString());
		assertEquals(null, items[0].getBusinessArea());

		// check destination item
		assertEquals(1, items[1].getLineNum());
		assertEquals(AccountType.GL_ACCOUNT, items[1].getAccountType());
		assertEquals(CreditDebitIndicator.DEBIT, items[1].getCDIndicator());
		assertEquals(TestData.AMOUNT_GL, items[1].getAmount());
		assertEquals(null, items[1].getCustomer());
		assertEquals(null, items[1].getVendor());
		assertEquals(TestData.GL_ACCOUNT_CASH, items[1].getGLAccount()
				.toString());
		assertEquals(null, items[1].getBusinessArea());
	}

	/**
	 * check ledger 2012_07
	 * 
	 * @param collection
	 */
	public static void checkLedger2012_08(HeadEntity[] docs) {
		assertEquals(2, docs.length);
		checkVendorDoc_08(docs[0], docs[1]);
		checkReverseDoc_08(docs[1]);
	}

	/**
	 * check vendor document in Month 8
	 * 
	 * @param vendorDoc
	 */
	private static void checkVendorDoc_08(HeadEntity vendorDoc,
			HeadEntity refDoc) {
		// check document number
		assertEquals(TestData.DOC_NUM1, vendorDoc.getDocumentNumber()
				.toString());
		// check posting date
		assertEquals(TestData.DATE_2012_08,
				TestData.DATE_FORMAT.format(vendorDoc.getPostingDate()));
		MonthIdentity monthId = vendorDoc.getMonthId();
		assertEquals(TestData.YEAR, monthId._fiscalYear);
		assertEquals(TestData.MONTH_08, monthId._fiscalMonth);
		// check document type
		assertEquals(DocumentType.VENDOR_INVOICE, vendorDoc.getDocumentType());
		// check text
		assertEquals(TestData.TEXT_VENDOR_DOC_CN, vendorDoc.getDocText());
		// check is reversed
		assertEquals(true, vendorDoc.IsReversed());
		assertEquals(refDoc.getDocIdentity(), vendorDoc.getReference());

		assertEquals(2, vendorDoc.getItemCount());
		ItemEntity[] items = vendorDoc.getItems();
		// check the vendor item
		assertEquals(0, items[0].getLineNum());
		assertEquals(AccountType.VENDOR, items[0].getAccountType());
		assertEquals(CreditDebitIndicator.CREDIT, items[0].getCDIndicator());
		assertEquals(TestData.AMOUNT_VENDOR, items[0].getAmount());
		assertEquals(null, items[0].getCustomer());
		assertEquals(TestData.VENDOR_BUS, items[0].getVendor().toString());
		assertEquals(TestData.GL_ACCOUNT_CASH, items[0].getGLAccount()
				.toString());
		assertEquals(null, items[0].getBusinessArea());

		// check cost item
		assertEquals(1, items[1].getLineNum());
		assertEquals(AccountType.GL_ACCOUNT, items[1].getAccountType());
		assertEquals(CreditDebitIndicator.DEBIT, items[1].getCDIndicator());
		assertEquals(TestData.AMOUNT_VENDOR, items[1].getAmount());
		assertEquals(null, items[1].getCustomer());
		assertEquals(null, items[1].getVendor());
		assertEquals(TestData.GL_ACCOUNT_COST, items[1].getGLAccount()
				.toString());
		assertEquals(TestData.BUSINESS_AREA_WORK, items[1].getBusinessArea()
				.toString());
	}

	/**
	 * check reverse document in Month 8
	 * 
	 * @param vendorDoc
	 */
	private static void checkReverseDoc_08(HeadEntity vendorDoc) {
		// check document number
		assertEquals(TestData.DOC_NUM2, vendorDoc.getDocumentNumber()
				.toString());
		// check posting date
		assertEquals(TestData.DATE_2012_08,
				TestData.DATE_FORMAT.format(vendorDoc.getPostingDate()));
		MonthIdentity monthId = vendorDoc.getMonthId();
		assertEquals(TestData.YEAR, monthId._fiscalYear);
		assertEquals(TestData.MONTH_08, monthId._fiscalMonth);
		// check document type
		assertEquals(DocumentType.VENDOR_INVOICE, vendorDoc.getDocumentType());
		// check text
		assertEquals(TestData.TEXT_VENDOR_DOC_CN, vendorDoc.getDocText());
		// check is reversed
		assertEquals(true, vendorDoc.IsReversed());
		assertEquals(vendorDoc.getDocIdentity(), vendorDoc.getReference());

		assertEquals(2, vendorDoc.getItemCount());
		ItemEntity[] items = vendorDoc.getItems();
		// check the vendor item
		assertEquals(0, items[0].getLineNum());
		assertEquals(AccountType.VENDOR, items[0].getAccountType());
		assertEquals(CreditDebitIndicator.DEBIT, items[0].getCDIndicator());
		assertEquals(TestData.AMOUNT_VENDOR, items[0].getAmount());
		assertEquals(null, items[0].getCustomer());
		assertEquals(TestData.VENDOR_BUS, items[0].getVendor().toString());
		assertEquals(TestData.GL_ACCOUNT_CASH, items[0].getGLAccount()
				.toString());
		assertEquals(null, items[0].getBusinessArea());

		// check cost item
		assertEquals(1, items[1].getLineNum());
		assertEquals(AccountType.GL_ACCOUNT, items[1].getAccountType());
		assertEquals(CreditDebitIndicator.CREDIT, items[1].getCDIndicator());
		assertEquals(TestData.AMOUNT_VENDOR, items[1].getAmount());
		assertEquals(null, items[1].getCustomer());
		assertEquals(null, items[1].getVendor());
		assertEquals(TestData.GL_ACCOUNT_COST, items[1].getGLAccount()
				.toString());
		assertEquals(TestData.BUSINESS_AREA_WORK, items[1].getBusinessArea()
				.toString());
	}

}

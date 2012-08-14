package com.jasonzqshen.familyAccountingBackendTest.core;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jasonzqshen.familyAccountingBackendTest.utils.DocumentCreater;
import com.jasonzqshen.familyAccountingBackendTest.utils.MasterDataChecker;
import com.jasonzqshen.familyAccountingBackendTest.utils.MasterDataCreater;
import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyAccountingBackendTest.utils.TransactionDataChecker;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.BalanceNotZero;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.format.CurrencyAmountFormatException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.transaction.DocumentIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;

public class TransactionDataCreationTester extends TesterBase {

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		TestUtilities
				.establishFolder2012_08(TestUtilities.TEST_ROOT_TRAN_CREATION);
		coreDriver.setRootPath(TestUtilities.TEST_ROOT_TRAN_CREATION);
		assertEquals(true, coreDriver.isInitialized());

		MasterDataCreater.createMasterData(coreDriver);

		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");

		// month 08, reverse document
		Date date = format.parse("2012.08.02");
		HeadEntity headEntity = DocumentCreater.createVendorDoc(coreDriver,
				date);
		DocumentIdentity docId = headEntity.getDocIdentity();
		TransactionDataManagement management = coreDriver
				.getTransDataManagement();
		HeadEntity reverseEntity = management.reverseDocument(docId);
		reverseEntity.setDocText(TestUtilities.TEST_DESCP);

		// store
		reverseEntity.save(true);

	}

	/**
	 * create entity
	 * 
	 * @param coreDriver
	 * @param date
	 * @param index
	 * @return
	 * @throws NullValueNotAcceptable
	 * @throws MasterDataIdentityNotDefined
	 * @throws IdentityTooLong
	 * @throws IdentityNoData
	 * @throws IdentityInvalidChar
	 * @throws MandatoryFieldIsMissing
	 * @throws BalanceNotZero
	 * @throws CurrencyAmountFormatException
	 */
	public HeadEntity createHeadEntity(CoreDriver coreDriver, Date date,
			int index) throws NullValueNotAcceptable,
			MasterDataIdentityNotDefined, IdentityTooLong, IdentityNoData,
			IdentityInvalidChar, MandatoryFieldIsMissing, BalanceNotZero,
			CurrencyAmountFormatException {
		HeadEntity headEntity = new HeadEntity(coreDriver,
				coreDriver.getMasterDataManagement());
		headEntity.setPostingDate(date);
		headEntity.setDocumentType(DocumentType.GL);
		headEntity.setDocText(TestUtilities.TEST_DESCP);

		ItemEntity item1 = headEntity.createEntity();
		item1.setGLAccount(new MasterDataIdentity_GLAccount(
				TestUtilities.GL_ACCOUNT1));
		item1.setAmount(CreditDebitIndicator.DEBIT,
				CurrencyAmount.parse(TestUtilities.TEST_AMOUNT1));
		item1.setBusinessArea(new MasterDataIdentity(
				TestUtilities.BUSINESS_AREA));

		ItemEntity item2 = headEntity.createEntity();
		MasterDataIdentity_GLAccount account2 = new MasterDataIdentity_GLAccount(
				TestUtilities.GL_ACCOUNT2);
		if (index == 0) {
			item2.setVendor(new MasterDataIdentity(TestUtilities.VENDOR),
					account2);
		} else {
			item2.setCustomer(new MasterDataIdentity(TestUtilities.CUSTOMER),
					account2);
		}
		item2.setAmount(CreditDebitIndicator.CREDIT,
				CurrencyAmount.parse(TestUtilities.TEST_AMOUNT1));
		boolean ret = headEntity.save(true);
		assertEquals(true, ret);
		return headEntity;
	}

	@Override
	protected void check(CoreDriver coreDriver) throws Exception {
		// reload from folder
		coreDriver.restart();

		MasterDataChecker.checkMasterData(coreDriver);

		TransactionDataManagement transManagement = coreDriver
				.getTransDataManagement();
		MonthIdentity[] monthIds = transManagement.getAllMonthIds();
		assertEquals(1, monthIds.length);

		TransactionDataChecker.checkLedger2012_08(transManagement
				.getCurrentLedger().getEntities());
	}

}

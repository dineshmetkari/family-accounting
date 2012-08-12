package com.jasonzqshen.familyAccountingBackendTest.core;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jasonzqshen.familyAccountingBackendTest.utils.MasterDataChecker;
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
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.transaction.DocumentIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;

public class TransactionDataCreationTester extends TesterBase {

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		TestUtilities.clearTestingRootFolder();
		TestUtilities.establishMasterData(coreDriver);

		// month 07
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		Date date = format.parse("2012.07.02");
		createHeadEntity(coreDriver, date, 0);
		createHeadEntity(coreDriver, date, 1);

		// month 08, reverse document
		date = format.parse("2012.08.02");
		HeadEntity headEntity = createHeadEntity(coreDriver, date, 0);
		DocumentIdentity docId = headEntity.getDocIdentity();
		TransactionDataManagement management = coreDriver
				.getTransDataManagement();
		HeadEntity reverseEntity = management.reverseDocument(docId);
		reverseEntity.setDocText(TestUtilities.TEST_DESCP);
		reverseEntity.save(true);

		coreDriver.restart();

		MasterDataChecker.checkMasterData(coreDriver);
		TransactionDataChecker.checkTransactionData(coreDriver);
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
	 */
	public HeadEntity createHeadEntity(CoreDriver coreDriver, Date date,
			int index) throws NullValueNotAcceptable,
			MasterDataIdentityNotDefined, IdentityTooLong, IdentityNoData,
			IdentityInvalidChar, MandatoryFieldIsMissing, BalanceNotZero {
		HeadEntity headEntity = new HeadEntity(coreDriver,
				coreDriver.getMasterDataManagement());
		headEntity.setPostingDate(date);
		headEntity.setDocumentType(DocumentType.GL);
		headEntity.setDocText(TestUtilities.TEST_DESCP);

		ItemEntity item1 = headEntity.createEntity();
		item1.setGLAccount(new MasterDataIdentity_GLAccount(
				TestUtilities.GL_ACCOUNT1));
		item1.setAmount(CreditDebitIndicator.DEBIT, 100);
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
		item2.setAmount(CreditDebitIndicator.CREDIT, 100);
		boolean ret = headEntity.save(true);
		assertEquals(true, ret);
		return headEntity;
	}

}

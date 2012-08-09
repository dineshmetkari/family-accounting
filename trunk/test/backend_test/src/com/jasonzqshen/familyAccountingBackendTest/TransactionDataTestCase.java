package com.jasonzqshen.familyAccountingBackendTest;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.BalanceNotZero;
import com.jasonzqshen.familyaccounting.core.exception.DocumentIdentityFormatException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalMonthRangeException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalYearRangeException;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.transaction.DocumentIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.DocumentNumber;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;

public class TransactionDataTestCase {
	@Test
	public void testDocumentIdentity() throws IdentityTooLong, IdentityNoData,
			IdentityInvalidChar, FiscalYearRangeException,
			FiscalMonthRangeException, DocumentIdentityFormatException {
		DocumentNumber docNum = new DocumentNumber(
				TestUtilities.TEST_DOC_NUM.toCharArray());
		DocumentIdentity id = new DocumentIdentity(docNum, 2012, 07);
		String idStr = id.toString();
		assertEquals(TestUtilities.TEST_DOC_ID, idStr);
		DocumentIdentity newId = DocumentIdentity.parse(idStr);
		assertEquals(newId, id);
	}

	@Test
	public void testLoad() throws Exception {
		CoreDriver coreDriver = CoreDriver.getInstance();
		coreDriver.clear();
		try {
			TestUtilities.loadTransaction(TestUtilities.TEST_ROOT_FOLDER);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TestUtilities
					.saveLogFile("testTransactionDataLoad.txt", coreDriver);
		}
	}

	@Test
	public void testStore() throws Exception {
		CoreDriver coreDriver = CoreDriver.getInstance();
		coreDriver.clear();
		try {
			ArrayList<CoreMessage> messages = new ArrayList<CoreMessage>();
			TestUtilities.clearTestingRootFolder();
			TestUtilities.establishMasterData(messages);

			// create new transaction data

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
			HeadEntity reverseEntity = management.reverseDocument(docId,
					messages);
			reverseEntity.setDocText(TestUtilities.TEST_DESCP);
			reverseEntity.save(messages, true);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			TestUtilities.saveLogFile("testTransactionDataStore.txt",
					coreDriver);
		}

		// reload
		TestUtilities.loadTransaction(TestUtilities.TEST_ROOT_FOLDER_EMPTY);
	}

	/**
	 * create head entity
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
		HeadEntity headEntity = new HeadEntity(coreDriver);
		headEntity.setPostingDate(date);
		headEntity.setDocumentType(DocumentType.GL);
		headEntity.setDocText(TestUtilities.TEST_DESCP);

		ItemEntity item1 = headEntity.createEntity();
		item1.setGLAccount(new MasterDataIdentity_GLAccount(
				TestUtilities.GL_ACCOUNT1.toCharArray()));
		item1.setAmount(CreditDebitIndicator.DEBIT, 100);
		item1.setBusinessArea(new MasterDataIdentity(
				TestUtilities.BUSINESS_AREA.toCharArray()));

		ItemEntity item2 = headEntity.createEntity();
		MasterDataIdentity_GLAccount account2 = new MasterDataIdentity_GLAccount(
				TestUtilities.GL_ACCOUNT2.toCharArray());
		if (index == 0) {
			item2.setVendor(
					new MasterDataIdentity(TestUtilities.VENDOR.toCharArray()),
					account2);
		} else {
			item2.setCustomer(
					new MasterDataIdentity(TestUtilities.CUSTOMER.toCharArray()),
					account2);
		}
		item2.setAmount(CreditDebitIndicator.CREDIT, 100);
		ArrayList<CoreMessage> messages = new ArrayList<CoreMessage>();
		boolean ret = headEntity.save(messages, true);
		assertEquals(true, ret);
		return headEntity;
	}

	/**
	 * load file
	 * 
	 * @param rootFile
	 * @throws RootFolderNotExsits
	 * @throws SystemException
	 * @throws NoMasterDataFactoryClass
	 * @throws FiscalMonthRangeException
	 * @throws FiscalYearRangeException
	 * @throws ParseException
	 * @throws IdentityInvalidChar
	 * @throws IdentityNoData
	 * @throws IdentityTooLong
	 * @throws MasterDataFileFormatException
	 * @throws NoMasterDataFileException
	 */

}

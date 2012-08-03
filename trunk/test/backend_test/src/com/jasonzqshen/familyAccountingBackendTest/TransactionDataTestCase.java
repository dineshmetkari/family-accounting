package com.jasonzqshen.familyAccountingBackendTest;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.DocumentIdentityFormatException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalMonthRangeException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalYearRangeException;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityExists;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NoMasterDataFactoryClass;
import com.jasonzqshen.familyaccounting.core.exception.ParametersException;
import com.jasonzqshen.familyaccounting.core.exception.RootFolderNotExsits;
import com.jasonzqshen.familyaccounting.core.exception.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.transaction.DocumentIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.DocumentNumber;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.AccountType;
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
	public void testLoad() throws NoMasterDataFactoryClass, SystemException,
			RootFolderNotExsits, FiscalYearRangeException,
			FiscalMonthRangeException, ParseException, IdentityTooLong,
			IdentityNoData, IdentityInvalidChar {
		load(TestUtilities.TEST_ROOT_FOLDER);
	}

	public void testStore() throws NoMasterDataFactoryClass, SystemException,
			RootFolderNotExsits, IdentityTooLong, IdentityNoData,
			IdentityInvalidChar, ParametersException, MasterDataIdentityExists,
			MasterDataIdentityNotDefined, FiscalYearRangeException,
			FiscalMonthRangeException {
		ArrayList<CoreMessage> messages = new ArrayList<CoreMessage>();
		CoreDriver coreDriver = TestUtilities.establishMasterData(messages);

		// create new transaction data

		// reload

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
	 */
	public void load(String rootFile) throws NoMasterDataFactoryClass,
			SystemException, RootFolderNotExsits, FiscalYearRangeException,
			FiscalMonthRangeException, ParseException, IdentityTooLong,
			IdentityNoData, IdentityInvalidChar {
		CoreDriver coreDriver = CoreDriver.getInstance();

		// set root path
		coreDriver.setRootPath(rootFile);
		coreDriver.setStartMonthID(new MonthIdentity(2012, 7));

		// initialize
		ArrayList<CoreMessage> messages = new ArrayList<CoreMessage>();
		coreDriver.init(messages);
		if (messages.size() > 0) {
			for (CoreMessage m : messages) {
				System.out.println(m.toString());
			}
		}
		assertEquals(0, messages.size());

		// get the count of identity
		final Calendar calendar = Calendar.getInstance();
		int curYear = calendar.get(Calendar.YEAR);
		int curMonth = calendar.get(Calendar.MONTH) + 1;
		int monthCount = curMonth - 7 + (curYear - 2012) * 12 + 1;

		TransactionDataManagement transManagement = coreDriver
				.getTransDataManagement();
		MonthIdentity[] monthIds = transManagement.getAllMonthIds();
		assertEquals(monthCount, monthIds.length);

		for (MonthIdentity monthId : monthIds) {
			HeadEntity[] collection = transManagement.getDocs(
					monthId._fiscalYear, monthId._fiscalMonth);
			assertEquals(TestUtilities.DOCUMNET_NUMS.length, collection.length);

			// check values
			for (int i = 0; i < collection.length; ++i) {
				HeadEntity head = collection[i];
				assertEquals(TestUtilities.DOCUMNET_NUMS[i], head
						.getDocumentNumber().toString());
				assertEquals(TestUtilities.TEST_DESCP, head.getDocText());
				assertEquals(monthId._fiscalYear, head.getFiscalYear());
				assertEquals(monthId._fiscalMonth, head.getFiscalMonth());
				// date
				SimpleDateFormat dateForm = new SimpleDateFormat("yyyy.MM.dd");
				Date date = null;
				if (monthId._fiscalMonth < 10) {
					date = dateForm.parse(String.format("%d.0%d.0%d",
							monthId._fiscalYear, monthId._fiscalMonth, 2));
				} else {
					date = dateForm.parse(String.format("%d.%d.0%d",
							monthId._fiscalYear, monthId._fiscalMonth, 2));
				}
				assertEquals(date, head.getPostingDate());

				assertEquals(DocumentType.GL, head.getDocumentType());
				if (i == 0) {
					assertEquals(true, head.IsReversed());

					DocumentIdentity refId = head.getReference();
					HeadEntity refEntity = transManagement.getEntity(refId);
					assertEquals(collection[1], refEntity);
				} else {
					assertEquals(false, head.IsReversed());
				}

				ItemEntity[] items = head.getItems();
				assertEquals(2, items.length);

				for (int j = 0; j < items.length; ++j) {
					ItemEntity item = items[j];

					assertEquals(j, item.getLineNum());

					// check account
					if (j == 0) {
						assertEquals(AccountType.GL_ACCOUNT,
								item.getAccountType());
						assertEquals(new MasterDataIdentity_GLAccount(
								TestUtilities.GL_ACCOUNT1.toCharArray()),
								item.getGLAccount());
						assertEquals(null, item.getCustomer());
						assertEquals(null, item.getVendor());
						assertEquals(CreditDebitIndicator.DEBIT,
								item.getCDIndicator());
						assertEquals(new MasterDataIdentity(
								TestUtilities.BUSINESS_AREA.toCharArray()),
								item.getBusinessArea());
					} else {
						assertEquals(new MasterDataIdentity_GLAccount(
								TestUtilities.GL_ACCOUNT2.toCharArray()),
								item.getGLAccount());
						if (i == 0) {
							assertEquals(AccountType.VENDOR,
									item.getAccountType());

							assertEquals(new MasterDataIdentity(
									TestUtilities.VENDOR.toCharArray()),
									item.getVendor());
							assertEquals(null, item.getCustomer());

						} else {
							assertEquals(AccountType.CUSTOMER,
									item.getAccountType());
							assertEquals(new MasterDataIdentity(
									TestUtilities.CUSTOMER.toCharArray()),
									item.getCustomer());
							assertEquals(null, item.getVendor());
						}

						assertEquals(CreditDebitIndicator.CREDIT,
								item.getCDIndicator());
						assertEquals(null, item.getBusinessArea());
					}

					assertEquals((int) (TestUtilities.AMOUNT * 100),
							(int) (item.getAmount() * 100));
				}
			}
		}
	}
}

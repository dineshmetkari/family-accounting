package com.jasonzqshen.familyAccountingBackendTest.utils;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.format.CurrencyAmountFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;

public class DocumentCreater {
	public static HeadEntity createVendorDoc(CoreDriver coreDriver, Date date) {
		try {
			HeadEntity headEntity = new HeadEntity(coreDriver,
					coreDriver.getMasterDataManagement());
			headEntity.setPostingDate(date);
			headEntity.setDocumentType(DocumentType.VENDOR_INVOICE);
			headEntity.setDocText(TestUtilities.TEST_DESCP);

			ItemEntity item1 = headEntity.createEntity();
			item1.setGLAccount(new MasterDataIdentity_GLAccount(
					TestUtilities.GL_ACCOUNT_COST));
			item1.setAmount(CreditDebitIndicator.DEBIT, CurrencyAmount.parse(TestUtilities.TEST_AMOUNT1));
			item1.setBusinessArea(new MasterDataIdentity(
					TestUtilities.BUSINESS_AREA));

			ItemEntity item2 = headEntity.createEntity();
			MasterDataIdentity_GLAccount account2 = new MasterDataIdentity_GLAccount(
					TestUtilities.GL_ACCOUNT2);
			item2.setVendor(new MasterDataIdentity(TestUtilities.VENDOR),
					account2);

			item2.setAmount(CreditDebitIndicator.CREDIT, new CurrencyAmount(
					123.45));
			boolean ret = headEntity.save(true);
			assertEquals(true, ret);
			return headEntity;
		} catch (NullValueNotAcceptable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SystemException(e);
		} catch (MasterDataIdentityNotDefined e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SystemException(e);
		} catch (IdentityTooLong e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SystemException(e);
		} catch (IdentityNoData e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SystemException(e);
		} catch (IdentityInvalidChar e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SystemException(e);
		} catch (CurrencyAmountFormatException e) {
			// TODO Auto-generated catch block
						e.printStackTrace();
						throw new SystemException(e);
		}
	}

	public static HeadEntity createCustomerDoc(CoreDriver coreDriver, Date date) {
		try {
			HeadEntity headEntity = new HeadEntity(coreDriver,
					coreDriver.getMasterDataManagement());
			headEntity.setPostingDate(date);
			headEntity.setDocumentType(DocumentType.CUSTOMER_INVOICE);
			headEntity.setDocText(TestUtilities.TEST_DESCP);

			ItemEntity item1 = headEntity.createEntity();
			item1.setGLAccount(new MasterDataIdentity_GLAccount(
					TestUtilities.GL_ACCOUNT_PROFIT));
			item1.setAmount(CreditDebitIndicator.CREDIT, CurrencyAmount.parse(TestUtilities.TEST_AMOUNT2));

			ItemEntity item2 = headEntity.createEntity();
			MasterDataIdentity_GLAccount account2 = new MasterDataIdentity_GLAccount(
					TestUtilities.GL_ACCOUNT2);
			item2.setCustomer(new MasterDataIdentity(TestUtilities.CUSTOMER),
					account2);
			item2.setAmount(CreditDebitIndicator.DEBIT, CurrencyAmount.parse(TestUtilities.TEST_AMOUNT2));
			boolean ret = headEntity.save(true);
			assertEquals(true, ret);
			return headEntity;
		} catch (NullValueNotAcceptable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SystemException(e);
		} catch (MasterDataIdentityNotDefined e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SystemException(e);
		} catch (IdentityTooLong e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SystemException(e);
		} catch (IdentityNoData e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SystemException(e);
		} catch (IdentityInvalidChar e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SystemException(e);
		} catch (CurrencyAmountFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SystemException(e);
		}
		
	}
}

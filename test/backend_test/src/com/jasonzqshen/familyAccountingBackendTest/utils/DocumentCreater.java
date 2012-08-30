package com.jasonzqshen.familyAccountingBackendTest.utils;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;

public class DocumentCreater {
    public static HeadEntity createVendorDoc(CoreDriver coreDriver, Date date) {
        try {
            HeadEntity headEntity = new HeadEntity(coreDriver,
                    coreDriver.getMasterDataManagement());
            headEntity.setPostingDate(date);
            headEntity.setDocumentType(DocumentType.VENDOR_INVOICE);
            headEntity.setDocText(TestData.TEXT_VENDOR_DOC);

            ItemEntity item1 = headEntity.createEntity();
            MasterDataIdentity_GLAccount rec_account = new MasterDataIdentity_GLAccount(
                    TestData.GL_ACCOUNT_CASH);
            item1.setVendor(new MasterDataIdentity(TestData.VENDOR_BUS),
                    rec_account);
            item1.setAmount(CreditDebitIndicator.CREDIT, TestData.AMOUNT_VENDOR);

            ItemEntity item2 = headEntity.createEntity();
            item2.setGLAccount(new MasterDataIdentity_GLAccount(
                    TestData.GL_ACCOUNT_COST));
            item2.setAmount(CreditDebitIndicator.DEBIT, TestData.AMOUNT_VENDOR);
            item2.setBusinessArea(new MasterDataIdentity(
                    TestData.BUSINESS_AREA_WORK));

            boolean ret = headEntity.save(true);
            assertEquals(true, ret);
            return headEntity;
        } catch (NullValueNotAcceptable e) {
            e.printStackTrace();
            throw new SystemException(e);
        } catch (MasterDataIdentityNotDefined e) {
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
        }
    }

    public static HeadEntity createCustomerDoc(CoreDriver coreDriver, Date date) {
        try {
            HeadEntity headEntity = new HeadEntity(coreDriver,
                    coreDriver.getMasterDataManagement());
            headEntity.setPostingDate(date);
            headEntity.setDocumentType(DocumentType.CUSTOMER_INVOICE);
            headEntity.setDocText(TestData.TEXT_CUSTOMER_DOC);

            ItemEntity item1 = headEntity.createEntity();
            item1.setGLAccount(new MasterDataIdentity_GLAccount(
                    TestData.GL_ACCOUNT_REV));
            item1.setAmount(CreditDebitIndicator.CREDIT,
                    TestData.AMOUNT_CUSTOMER);

            ItemEntity item2 = headEntity.createEntity();
            MasterDataIdentity_GLAccount account2 = new MasterDataIdentity_GLAccount(
                    TestData.GL_ACCOUNT_BANK);
            item2.setCustomer(new MasterDataIdentity(TestData.CUSTOMER1),
                    account2);
            item2.setAmount(CreditDebitIndicator.DEBIT,
                    TestData.AMOUNT_CUSTOMER);
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
        }

    }
    
    public static HeadEntity createGLDoc(CoreDriver coreDriver, Date date) {
        try {
            HeadEntity headEntity = new HeadEntity(coreDriver,
                    coreDriver.getMasterDataManagement());
            headEntity.setPostingDate(date);
            headEntity.setDocumentType(DocumentType.GL);
            headEntity.setDocText(TestData.TEXT_GL_DOC);

            ItemEntity item1 = headEntity.createEntity();
            item1.setGLAccount(new MasterDataIdentity_GLAccount(
                    TestData.GL_ACCOUNT_BANK));
            item1.setAmount(CreditDebitIndicator.CREDIT,
                    TestData.AMOUNT_GL);

            ItemEntity item2 = headEntity.createEntity();
            item2.setGLAccount(new MasterDataIdentity_GLAccount(
                    TestData.GL_ACCOUNT_CASH));
            item2.setAmount(CreditDebitIndicator.DEBIT,
                    TestData.AMOUNT_GL);
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
        }

    }
}

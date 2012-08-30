package com.jasonzqshen.familyAccountingBackendTest.entry;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jasonzqshen.familyAccountingBackendTest.utils.DocumentCreater;
import com.jasonzqshen.familyAccountingBackendTest.utils.MasterDataCreater;
import com.jasonzqshen.familyAccountingBackendTest.utils.TestData;
import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyAccountingBackendTest.utils.TransactionDataChecker;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.document_entries.CustomerEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.GLAccountEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.VendorEntry;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.transaction.DocumentIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;

public class EntriesTransactionTester extends TesterBase {

    @Override
    protected void doTest(CoreDriver coreDriver) throws Exception {
        TestUtilities
                .establishFolder2012_07(TestUtilities.TEST_ROOT_LEDGER_CLOSING);
        coreDriver.setRootPath(TestUtilities.TEST_ROOT_LEDGER_CLOSING);
        assertEquals(true, coreDriver.isInitialized());

        MasterDataCreater.createMasterData(coreDriver);

        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");

        // month 08, reverse document
        Date date = format.parse("2012.07.02");
        // ledger
        VendorEntry vendorEntry = new VendorEntry(coreDriver);
        vendorEntry.setValue(VendorEntry.POSTING_DATE, date);
        vendorEntry.setValue(VendorEntry.TEXT, TestData.TEXT_VENDOR_DOC);
        vendorEntry.setValue(VendorEntry.VENDOR, new MasterDataIdentity(
                TestData.VENDOR_BUS));
        vendorEntry.setValue(VendorEntry.REC_ACC,
                new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_CASH));
        vendorEntry.setValue(VendorEntry.GL_ACCOUNT,
                new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_COST));
        vendorEntry.setValue(VendorEntry.BUSINESS_AREA,
                new MasterDataIdentity(TestData.BUSINESS_AREA_WORK));
        vendorEntry.setValue(VendorEntry.AMOUNT, TestData.AMOUNT_VENDOR);
        vendorEntry.save(true);

        // customer
        CustomerEntry customerEntry = new CustomerEntry(coreDriver);
        customerEntry.setValue(CustomerEntry.POSTING_DATE, date);
        customerEntry.setValue(CustomerEntry.TEXT, TestData.TEXT_CUSTOMER_DOC);
        customerEntry.setValue(CustomerEntry.CUSTOMER, new MasterDataIdentity(
                TestData.CUSTOMER1));
        customerEntry.setValue(CustomerEntry.REC_ACC,
                new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_BANK));
        customerEntry.setValue(CustomerEntry.GL_ACCOUNT,
                new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_REV));
        customerEntry.setValue(CustomerEntry.AMOUNT, TestData.AMOUNT_CUSTOMER);
        customerEntry.save(true);
        
        // GL
        GLAccountEntry glAccEntry = new GLAccountEntry(coreDriver);
        glAccEntry.setValue(GLAccountEntry.POSTING_DATE, date);
        glAccEntry.setValue(GLAccountEntry.TEXT, TestData.TEXT_GL_DOC);
        glAccEntry.setValue(GLAccountEntry.SRC_ACCOUNT,
                new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_BANK));
        glAccEntry.setValue(GLAccountEntry.DST_ACCOUNT,
                new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_CASH));
        glAccEntry.setValue(GLAccountEntry.AMOUNT, TestData.AMOUNT_GL);
        glAccEntry.save(true);

        TransactionDataManagement transManagement = coreDriver
                .getTransDataManagement();
        transManagement.monthEndClose();

        date = format.parse("2012.08.02");
        HeadEntity headEntity = DocumentCreater.createVendorDoc(coreDriver,
                date);
        DocumentIdentity docId = headEntity.getDocIdentity();
        transManagement.reverseDocument(docId);

    }

    @Override
    protected void check(CoreDriver coreDriver) throws Exception {
        TransactionDataChecker.checkTransactionData(coreDriver);

        // reload
        coreDriver.restart();
        TransactionDataChecker.checkTransactionData(coreDriver);

    }

}

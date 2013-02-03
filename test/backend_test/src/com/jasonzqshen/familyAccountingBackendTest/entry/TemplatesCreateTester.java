package com.jasonzqshen.familyAccountingBackendTest.entry;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import com.jasonzqshen.familyAccountingBackendTest.utils.MasterDataCreater;
import com.jasonzqshen.familyAccountingBackendTest.utils.TestData;
import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.document_entries.CustomerEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.EntryTemplate;
import com.jasonzqshen.familyaccounting.core.document_entries.EntryTemplatesManagement;
import com.jasonzqshen.familyaccounting.core.document_entries.GLAccountEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.VendorEntry;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;

public class TemplatesCreateTester extends TesterBase {
    EntryTemplatesManagement tempMgmt;

    @Override
    protected void doTest(CoreDriver coreDriver) throws Exception {
        TestUtilities
                .clearTestingRootFolder(TestUtilities.TEST_ROOT_TEMPLATE_CREATION);

        tempMgmt = new EntryTemplatesManagement(coreDriver);
        coreDriver.setRootPath(TestUtilities.TEST_ROOT_TEMPLATE_CREATION);

        tempMgmt.initialize();
        assertEquals(true, coreDriver.isInitialized());
        MasterDataCreater.createMasterData(coreDriver);

        // create template
        createTemplate(coreDriver, tempMgmt);
    }

    public static void createTemplate(CoreDriver coreDriver,
            EntryTemplatesManagement tempMgmt) throws Exception {
        // vendor
        VendorEntry vendorEntry = new VendorEntry(coreDriver);
        vendorEntry.setValue(VendorEntry.AMOUNT, TestData.AMOUNT_VENDOR);
        vendorEntry.setValue(VendorEntry.VENDOR, new MasterDataIdentity(
                TestData.VENDOR_BUS));
        vendorEntry.setValue(VendorEntry.TEXT, TestData.TEXT_VENDOR_DOC);
        vendorEntry.setValue(VendorEntry.REC_ACC,
                new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_CASH));
        vendorEntry.setValue(VendorEntry.GL_ACCOUNT,
                new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_COST));
        vendorEntry.setValue(VendorEntry.BUSINESS_AREA, new MasterDataIdentity(
                TestData.BUSINESS_AREA_WORK));
        tempMgmt.saveAsTemplate(vendorEntry, TestData.TEXT_VENDOR_DOC);

        // GL
        GLAccountEntry glEntry = new GLAccountEntry(coreDriver);
        glEntry.setValue(GLAccountEntry.TEXT, TestData.TEXT_GL_DOC);
        glEntry.setValue(GLAccountEntry.SRC_ACCOUNT,
                new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_BANK));
        glEntry.setValue(GLAccountEntry.DST_ACCOUNT,
                new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_CASH));
        tempMgmt.saveAsTemplate(glEntry, TestData.TEXT_GL_DOC);

        // customer
        CustomerEntry customerEntry = new CustomerEntry(coreDriver);
        customerEntry.setValue(CustomerEntry.TEXT, TestData.TEXT_CUSTOMER_DOC);
        customerEntry.setValue(CustomerEntry.CUSTOMER, new MasterDataIdentity(
                TestData.CUSTOMER1));
        customerEntry.setValue(CustomerEntry.REC_ACC,
                new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_BANK));
        customerEntry.setValue(CustomerEntry.GL_ACCOUNT,
                new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_REV));
        tempMgmt.saveAsTemplate(customerEntry, TestData.TEXT_CUSTOMER_DOC);
    }

    @Override
    protected void check(CoreDriver coreDriver) throws Exception {
        ArrayList<EntryTemplate> templates = tempMgmt.getEntryTemplates();
        TemplatesLoadingTester.checkTemplate(templates);

        tempMgmt.clear();
        tempMgmt.initialize();
        templates = tempMgmt.getEntryTemplates();
        TemplatesLoadingTester.checkTemplate(templates);
    }

}

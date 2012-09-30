package com.jasonzqshen.familyAccountingBackendTest.entry;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;

import com.jasonzqshen.familyAccountingBackendTest.utils.TestData;
import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.document_entries.CustomerEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.EntryTemplate;
import com.jasonzqshen.familyaccounting.core.document_entries.EntryTemplatesManagement;
import com.jasonzqshen.familyaccounting.core.document_entries.GLAccountEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.IDocumentEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.VendorEntry;

public class TemplatesLoadingTester extends TesterBase {
    EntryTemplatesManagement tempMgmt;

    @Override
    protected void doTest(CoreDriver coreDriver) throws Exception {
        tempMgmt = new EntryTemplatesManagement(coreDriver);
        coreDriver.setRootPath(TestUtilities.TEST_LOAD_TEMPLATE);
        tempMgmt.initialize();
    }

    @Override
    protected void check(CoreDriver coreDriver) throws Exception {
        ArrayList<EntryTemplate> templates = tempMgmt.getEntryTemplates();
        checkTemplate(templates);
    }

    /**
     * check templates
     * 
     * @param templates
     */
    public static void checkTemplate(ArrayList<EntryTemplate> templates) {
        assertEquals(3, templates.size());

        for (int i = 0; i < 3; ++i) {
            EntryTemplate temp = templates.get(i);
            assertEquals(i + 1, temp.getIdentity());

            switch (i + 1) {
            case 1:
                // vendor
                assertEquals(EntryTemplate.VENDOR_ENTRY_TYPE,
                        temp.getEntryType());
                assertEquals("Traffic expense on work", temp.getName());
                assertEquals(TestData.VENDOR_BUS,
                        temp.getDefaultValue(VendorEntry.VENDOR).toString());
                assertEquals(TestData.GL_ACCOUNT_CASH,
                        temp.getDefaultValue(VendorEntry.REC_ACC).toString());
                assertEquals(TestData.GL_ACCOUNT_COST,
                        temp.getDefaultValue(VendorEntry.GL_ACCOUNT).toString());
                assertEquals(TestData.BUSINESS_AREA_WORK,
                        temp.getDefaultValue(VendorEntry.BUSINESS_AREA)
                                .toString());
                assertEquals(TestData.AMOUNT_VENDOR,
                        temp.getDefaultValue(IDocumentEntry.AMOUNT));
                assertEquals(TestData.TEXT_VENDOR_DOC,
                        temp.getDefaultValue(IDocumentEntry.TEXT));

                break;
            case 2:
                // G/L
                assertEquals(EntryTemplate.GL_ENTRY_TYPE, temp.getEntryType());
                assertEquals(TestData.TEXT_GL_DOC, temp.getName());
                assertEquals(TestData.GL_ACCOUNT_BANK,
                        temp.getDefaultValue(GLAccountEntry.SRC_ACCOUNT)
                                .toString());
                assertEquals(TestData.GL_ACCOUNT_CASH,
                        temp.getDefaultValue(GLAccountEntry.DST_ACCOUNT)
                                .toString());
                break;

            case 3:

                // customer
                assertEquals(EntryTemplate.CUSTOMER_ENTRY_TYPE,
                        temp.getEntryType());
                assertEquals(TestData.TEXT_CUSTOMER_DOC, temp.getName());
                assertEquals(TestData.CUSTOMER1,
                        temp.getDefaultValue(CustomerEntry.CUSTOMER).toString());
                assertEquals(TestData.GL_ACCOUNT_BANK,
                        temp.getDefaultValue(CustomerEntry.REC_ACC).toString());
                assertEquals(TestData.GL_ACCOUNT_REV,
                        temp.getDefaultValue(CustomerEntry.GL_ACCOUNT)
                                .toString());

                assertEquals(TestData.TEXT_CUSTOMER_DOC,
                        temp.getDefaultValue(IDocumentEntry.TEXT));
                break;
            }
        }
    }
}

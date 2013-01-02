using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore;
using WinStore_FamilyAccountingCore.DocumentEntries;
using WinStore_FamilyAccountingCore.MasterData;

namespace WinStore_FamilyAccountingUnitTest.Utils
{
    public class TemplateCreater
    {
        public async static Task CreateTemplate(CoreDriver coreDriver,
            EntryTemplatesManagement tempMgmt)
        {
            // vendor
            VendorEntry vendorEntry = new VendorEntry(coreDriver, coreDriver.MdMgmt);
            vendorEntry.SetValue(EntryTemplate.AMOUNT, TestData.AMOUNT_VENDOR);
            vendorEntry.SetValue(VendorEntry.VENDOR, new MasterDataIdentity(
                    TestData.VENDOR_BUS));
            vendorEntry.SetValue(EntryTemplate.TEXT, TestData.TEXT_VENDOR_DOC);
            vendorEntry.SetValue(VendorEntry.REC_ACC,
                    new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_CASH));
            vendorEntry.SetValue(VendorEntry.GL_ACCOUNT,
                    new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_COST));
            vendorEntry.SetValue(VendorEntry.BUSINESS_AREA, new MasterDataIdentity(
                    TestData.BUSINESS_AREA_WORK));
            await tempMgmt.SaveAsTemplate(vendorEntry, TestData.TEXT_VENDOR_DOC);

            // GL
            GLAccountEntry glEntry = new GLAccountEntry(coreDriver, coreDriver.MdMgmt);
            glEntry.SetValue(EntryTemplate.TEXT, TestData.TEXT_GL_DOC);
            glEntry.SetValue(GLAccountEntry.SRC_ACCOUNT,
                    new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_BANK));
            glEntry.SetValue(GLAccountEntry.DST_ACCOUNT,
                    new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_CASH));
            await tempMgmt.SaveAsTemplate(glEntry, TestData.TEXT_GL_DOC);

            // customer
            CustomerEntry customerEntry = new CustomerEntry(coreDriver, coreDriver.MdMgmt);
            customerEntry.SetValue(EntryTemplate.TEXT, TestData.TEXT_CUSTOMER_DOC);
            customerEntry.SetValue(CustomerEntry.CUSTOMER, new MasterDataIdentity(
                    TestData.CUSTOMER1));
            customerEntry.SetValue(CustomerEntry.REC_ACC,
                    new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_BANK));
            customerEntry.SetValue(CustomerEntry.GL_ACCOUNT,
                    new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_REV));
            await tempMgmt.SaveAsTemplate(customerEntry, TestData.TEXT_CUSTOMER_DOC);
        }
    }
}

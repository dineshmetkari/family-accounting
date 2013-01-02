using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore;
using WinStore_FamilyAccountingCore.DocumentEntries;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Transaction;
using WinStore_FamilyAccountingUnitTest.Utils;

namespace WinStore_FamilyAccountingUnitTest.CreationTestCases
{
    [TestClass]
    public class EntriesTransactionTester
    {
        CoreDriver _coreDriver;
        [TestInitialize]
        public async Task Initialize()
        {
            _coreDriver = new CoreDriver();
            await StoragePreparation.EstablishFolder_2012_07(
                TestFolderPath.CREATING_TRANSDATA_WITH_ENTRY_PATH
                , _coreDriver);
        }

        /// <summary>
        /// create transaction data with entry
        /// </summary>
        [TestMethod]
        public async Task TestCreateTransDataWithEntry()
        {
            Assert.IsTrue(_coreDriver.IsInitialize);
            MasterDataCreater.CreateMasterData(_coreDriver);

            // month 08, reverse document
            DateTime date = DateTime.Parse("2012.07.02");
            // ledger
            VendorEntry vendorEntry = new VendorEntry(_coreDriver, _coreDriver.MdMgmt);
            vendorEntry.SetValue(EntryTemplate.POSTING_DATE, date);
            vendorEntry.SetValue(EntryTemplate.TEXT, TestData.TEXT_VENDOR_DOC);
            vendorEntry.SetValue(VendorEntry.VENDOR, new MasterDataIdentity(
                    TestData.VENDOR_BUS));
            vendorEntry.SetValue(VendorEntry.REC_ACC,
                    new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_CASH));
            vendorEntry.SetValue(VendorEntry.GL_ACCOUNT,
                    new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_COST));
            vendorEntry.SetValue(VendorEntry.BUSINESS_AREA, new MasterDataIdentity(
                    TestData.BUSINESS_AREA_WORK));
            vendorEntry.SetValue(EntryTemplate.AMOUNT, TestData.AMOUNT_VENDOR);
            await vendorEntry.SaveAsync(true);

            // customer
            CustomerEntry customerEntry = new CustomerEntry(_coreDriver, _coreDriver.MdMgmt);
            customerEntry.SetValue(EntryTemplate.POSTING_DATE, date);
            customerEntry.SetValue(EntryTemplate.TEXT, TestData.TEXT_CUSTOMER_DOC);
            customerEntry.SetValue(CustomerEntry.CUSTOMER, new MasterDataIdentity(
                    TestData.CUSTOMER1));
            customerEntry.SetValue(CustomerEntry.REC_ACC,
                    new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_BANK));
            customerEntry.SetValue(CustomerEntry.GL_ACCOUNT,
                    new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_REV));
            customerEntry.SetValue(EntryTemplate.AMOUNT, TestData.AMOUNT_CUSTOMER);
            await customerEntry.SaveAsync(true);

            // GL
            GLAccountEntry glAccEntry = new GLAccountEntry(_coreDriver, _coreDriver.MdMgmt);
            glAccEntry.SetValue(EntryTemplate.POSTING_DATE, date);
            glAccEntry.SetValue(EntryTemplate.TEXT, TestData.TEXT_GL_DOC);
            glAccEntry.SetValue(GLAccountEntry.SRC_ACCOUNT,
                    new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_BANK));
            glAccEntry.SetValue(GLAccountEntry.DST_ACCOUNT,
                    new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_CASH));
            glAccEntry.SetValue(EntryTemplate.AMOUNT, TestData.AMOUNT_GL);
            await glAccEntry.SaveAsync(true);

            TransactionDataManagement transManagement = _coreDriver.TransMgmt;

            date = DateTime.Parse("2012.08.02");
            HeadEntity headEntity = await DocumentCreater.CreateVendorDoc(_coreDriver,
                    date);
            DocumentIdentity docId = headEntity.DocIdentity;
            transManagement.ReverseDocument(docId);

            // check
            TransactionDataChecker.CheckTransactionData(_coreDriver);

            // reload
            await _coreDriver.RestartAsync();
            TransactionDataChecker.CheckTransactionData(_coreDriver);
        }
    }
}

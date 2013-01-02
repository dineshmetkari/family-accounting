using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore;
using WinStore_FamilyAccountingCore.DocumentEntries;
using WinStore_FamilyAccountingCore.Transaction;
using WinStore_FamilyAccountingUnitTest.Utils;

namespace WinStore_FamilyAccountingUnitTest.CreationTestCases
{
    [TestClass]
    public class CreateWithTemplateTester
    {
        CoreDriver _coreDriver;
        [TestInitialize]
        public async Task Initialize()
        {
            _coreDriver = new CoreDriver();
            await StoragePreparation.EstablishFolder_2012_07(
                TestFolderPath.CREATING_TRANSDATA_WITH_TEMPLATE
                , _coreDriver);
        }

        [TestMethod]
        public async Task TestCreateTransWithTemplate()
        {
            Assert.IsTrue(_coreDriver.IsInitialize);
            MasterDataCreater.CreateMasterData(_coreDriver);

            await TemplateCreater.CreateTemplate(_coreDriver, _coreDriver.TmMgmt);

            // create transaction data
            DateTime date = DateTime.Parse("2012.07.02");

            VendorEntry vendor = (VendorEntry)_coreDriver.TmMgmt.GetEntryTemplate(1)
                    .GenerateEntry();
            vendor.SetValue(EntryTemplate.POSTING_DATE, date);
            await vendor.SaveAsync(true);

            CustomerEntry customer = (CustomerEntry)_coreDriver.TmMgmt.GetEntryTemplate(3)
                    .GenerateEntry();
            customer.SetValue(EntryTemplate.POSTING_DATE, date);
            customer.SetValue(EntryTemplate.AMOUNT, TestData.AMOUNT_CUSTOMER);
            await customer.SaveAsync(true);

            GLAccountEntry glEntry = (GLAccountEntry)_coreDriver.TmMgmt.GetEntryTemplate(2)
                    .GenerateEntry();
            glEntry.SetValue(EntryTemplate.POSTING_DATE, date);
            glEntry.SetValue(EntryTemplate.AMOUNT, TestData.AMOUNT_GL);
            await glEntry.SaveAsync(true);

            TransactionDataManagement transManagement = _coreDriver.TransMgmt;

            // month 08
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

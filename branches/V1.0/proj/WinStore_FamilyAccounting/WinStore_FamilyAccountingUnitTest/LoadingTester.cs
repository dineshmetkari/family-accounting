using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using Windows.Storage;
using WinStore_FamilyAccountingUnitTest.Utils;
using WinStore_FamilyAccountingCore.Exceptions;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Utilities;
using WinStore_FamilyAccountingCore.DocumentEntries;

namespace WinStore_FamilyAccountingUnitTest
{
    [TestClass]
    public class LoadingTester
    {
        CoreDriver _coreDriver;

        [TestInitialize]
        public async Task Initialize()
        {
            StorageFolder folder = await StoragePreparation.PrepareDataForLoadingAsync(
                TestFolderPath.LOADING_DATA_PATH);
            _coreDriver = new CoreDriver();
            _coreDriver.RootFolder = folder;

            await _coreDriver.RestartAsync();
        }

        [TestMethod]
        public void TestLoadData()
        {
            MasterDataChecker.CheckMasterData(_coreDriver);
            TransactionDataChecker.CheckTransactionData(_coreDriver);
            AccountReportBalanceChecker.CheckAccountBalance(_coreDriver);
            TemplateChecker.CheckTemplate(_coreDriver.TmMgmt.EntryTemplates);
        }
    }
}

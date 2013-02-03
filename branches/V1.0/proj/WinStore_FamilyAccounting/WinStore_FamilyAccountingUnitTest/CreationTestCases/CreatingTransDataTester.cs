using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore;
using WinStore_FamilyAccountingCore.Transaction;
using WinStore_FamilyAccountingUnitTest.Utils;

namespace WinStore_FamilyAccountingUnitTest.CreationTestCases
{
    [TestClass]
    public class CreatingTransDataTester
    {
        CoreDriver _coreDriver;
        [TestInitialize]
        public async Task Initialize()
        {
            _coreDriver = new CoreDriver();
            await StoragePreparation.EstablishFolder_2012_07(
                TestFolderPath.CREATING_TRANSDATA_PATH
                , _coreDriver);
        }

        [TestMethod]
        public async Task TestCreateTransData()
        {
            Assert.IsTrue(_coreDriver.IsInitialize);
            MasterDataCreater.CreateMasterData(_coreDriver);

            DateTime date = DateTime.Parse("2012.07.02");
            // ledger
            await DocumentCreater.CreateVendorDoc(_coreDriver, date);
            await DocumentCreater.CreateCustomerDoc(_coreDriver, date);
            await DocumentCreater.CreateGLDoc(_coreDriver, date);

            await _coreDriver.RestartAsync();

            date = DateTime.Parse("2012.08.02");
            HeadEntity headEntity = await DocumentCreater.CreateVendorDoc(_coreDriver,
                    date);
            _coreDriver.TransMgmt.ReverseDocument(headEntity.DocIdentity);


            TransactionDataChecker.CheckTransactionData(_coreDriver);

            // reload
            await _coreDriver.RestartAsync();
            TransactionDataChecker.CheckTransactionData(_coreDriver);
        }
    }
}

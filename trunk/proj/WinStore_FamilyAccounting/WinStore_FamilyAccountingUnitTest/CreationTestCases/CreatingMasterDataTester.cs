using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore;
using WinStore_FamilyAccountingUnitTest.Utils;

namespace WinStore_FamilyAccountingUnitTest.CreationTestCases
{
    [TestClass]
    public class CreatingMasterDataTester
    {
        CoreDriver _coreDriver;
        [TestInitialize]
        public async Task Initialize()
        {
            _coreDriver = new CoreDriver();
            await StoragePreparation.EstablishFolder_2012_07(
                TestFolderPath.CREATING_MASTERDATA_PATH
                , _coreDriver);
        }

        [TestMethod]
        public async Task TestCreateMasterData()
        {
            Assert.IsTrue(_coreDriver.IsInitialize);
		    MasterDataCreater.CreateMasterData(_coreDriver);
            MasterDataChecker.CheckMasterData(_coreDriver);

            // store
            await _coreDriver.MdMgmt.StoreAsync();

            await _coreDriver.RestartAsync();
            MasterDataChecker.CheckMasterData(_coreDriver);
        }
    }
}

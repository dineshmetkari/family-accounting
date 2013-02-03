using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Storage;
using WinStore_FamilyAccountingCore;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Transaction;
using WinStore_FamilyAccountingUnitTest.Utils;

namespace WinStore_FamilyAccountingUnitTest
{
    [TestClass]
    public class LoadingEmptyTester
    {
        CoreDriver _coreDriver;

        [TestInitialize]
        public async Task Initialize()
        {
            StorageFolder folder = await StoragePreparation.CreateRootFolder(
                TestFolderPath.LOADING_EMPTY_DATA_PATH);
            _coreDriver = new CoreDriver();
            _coreDriver.RootFolder = folder;

            await _coreDriver.RestartAsync();
        }

        [TestMethod]
        public async Task TestLoadEmpty()
        {
            checkCore(_coreDriver);
            await checkFileSystem(_coreDriver.RootFolder);

            // restart
            await _coreDriver.RestartAsync();
            checkCore(_coreDriver);
        }

        /// <summary>
        /// check core driver
        /// </summary>
        /// <param name="coreDriver"></param>
        private void checkCore(CoreDriver coreDriver)
        {
            Assert.AreEqual(true, coreDriver.IsInitialize);

            MasterDataManagement masterData = coreDriver.MdMgmt;
            foreach (MasterDataType type in Enum.GetValues(typeof(MasterDataType)))
            {
                MasterDataFactoryBase factory = masterData
                        .GetMasterDataFactory(type);
                Assert.AreEqual(0, factory.AllEntities.Count);
            }

            TransactionDataManagement tranData = coreDriver
                    .TransMgmt;
            Assert.IsTrue(null != tranData);
            MonthIdentity[] monthIds = coreDriver.MonthIds;
            Assert.AreEqual(1, monthIds.Length);
        }

        /// <summary>
        /// check file system
        /// </summary>
        /// <param name="rootFolder"></param>
        private async Task checkFileSystem(StorageFolder rootFolder)
        {
            try
            {
                StorageFile metaFile = await rootFolder.GetFileAsync(CoreDriver.META_DATA);
                StorageFolder mdFolder = await rootFolder.GetFolderAsync(
                    MasterDataManagement.MASTER_DATA_FOLDER);
                StorageFolder transFolder = await rootFolder.GetFolderAsync(
                   TransactionDataManagement.TRANSACTION_DATA_FOLDER);

                IReadOnlyList<StorageFile> files = await mdFolder.GetFilesAsync();
                Assert.AreEqual(Enum.GetValues(typeof(MasterDataType)).Length, files.Count);

                files = await transFolder.GetFilesAsync();
                Assert.AreEqual(1, files.Count);
            }
            catch (Exception e)
            {
                Assert.Fail(e.ToString());
            }
        }
    }
}

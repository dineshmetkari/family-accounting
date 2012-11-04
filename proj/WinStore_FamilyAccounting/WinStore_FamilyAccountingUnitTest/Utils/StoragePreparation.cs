using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.ApplicationModel;
using Windows.Storage;
using WinStore_FamilyAccountingCore;
using WinStore_FamilyAccountingCore.DocumentEntries;
using WinStore_FamilyAccountingCore.Exceptions;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Transaction;

namespace WinStore_FamilyAccountingUnitTest.Utils
{
    public class StoragePreparation
    {
        public static readonly string DATA = "DATA";
        public static readonly string TEST_DATA_PATH = "test_data";
        /// <summary>
        /// prepare data for loading test cases
        /// </summary>
        /// <param name="name">name of path</param>
        /// <returns></returns>
        public static async Task<StorageFolder> PrepareDataForLoadingAsync(string name)
        {
            // get source folder
            StorageFolder appFolder = Package.Current.InstalledLocation;
            StorageFolder dataFolder = await appFolder.GetFolderAsync(DATA);
            StorageFolder orgFolder = await dataFolder.GetFolderAsync(TEST_DATA_PATH);
            
            StorageFolder dstFolder = await ApplicationData.Current
                .LocalFolder.CreateFolderAsync(name, CreationCollisionOption.OpenIfExists);

            // copy metadata.txt
            StorageFile metadataFile = await orgFolder.GetFileAsync(
                CoreDriver.META_DATA);
            await metadataFile.CopyAsync(dstFolder);
            
            // copy template.xml
            string templateFileName = EntryTemplatesManagement.FILE_NAME;
            StorageFile templatesFile = await orgFolder.GetFileAsync(
                templateFileName);
            await templatesFile.CopyAsync(dstFolder);

            #region create master_data folder
            StorageFolder dstMdFolder = await dstFolder.CreateFolderAsync(
                MasterDataManagement.MASTER_DATA_FOLDER);
            StorageFolder orgMdFolder = await orgFolder.GetFolderAsync(MasterDataManagement.MASTER_DATA_FOLDER);

            foreach (StorageFile file in await orgMdFolder.GetFilesAsync())
            {
                await file.CopyAsync(dstMdFolder);
            }
            #endregion

            #region create transaction_data folder
            StorageFolder dstTrFolder = await dstFolder.CreateFolderAsync(
                TransactionDataManagement.TRANSACTION_DATA_FOLDER);
            StorageFolder orgTrFolder = await orgFolder.GetFolderAsync(
                TransactionDataManagement.TRANSACTION_DATA_FOLDER);

            foreach (StorageFile file in await orgTrFolder.GetFilesAsync())
            {
                await file.CopyAsync(dstTrFolder);
            }
            #endregion

            return dstFolder;
        }

        /// <summary>
        /// create root folder for test case
        /// </summary>
        /// <param name="name"></param>
        /// <returns></returns>
        public static async Task<StorageFolder> CreateRootFolder(string name)
        {
            StorageFolder dstFolder = await ApplicationData.Current
               .LocalFolder.CreateFolderAsync(name, CreationCollisionOption.OpenIfExists);
            return dstFolder;
        }

        /// <summary>
        /// create root folder for creation test in 2012_07
        /// </summary>
        /// <param name="name"></param>
        /// <param name="coreDriver"></param>
        /// <returns></returns>
        public static async Task EstablishFolder_2012_07(String name
            , CoreDriver coreDriver)
        {
            coreDriver.RootFolder = await CreateRootFolder(name);
            await coreDriver.RestartAsync();
            MonthIdentity monthId;
            monthId = new MonthIdentity(2012, 7);
            coreDriver.SetStartMonthId(monthId);
        }

        /// <summary>
        /// prepare data for loading investment test cases
        /// </summary>
        public static void PrepareDataForLoadingInvestment()
        {
           
        }
    }
}

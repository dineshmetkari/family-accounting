using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Security;
using System.Text;
using System.Threading.Tasks;
using System.Xml;
using System.Xml.Linq;
using Windows.Storage;
using Windows.Storage.Streams;
using WinStore_FamilyAccountingCore.Exceptions;
using WinStore_FamilyAccountingCore.Exceptions.FormatExceptions;
using WinStore_FamilyAccountingCore.Utilities;

namespace WinStore_FamilyAccountingCore.MasterData
{
    /// <summary>
    /// master data utilities
    /// </summary>
    public class MasterDataUtils
    {
        public static readonly String DATE_FORMAT = "yyyy.MM.dd";

        public static readonly String XML_ID = "id"; // identity
        public static readonly String XML_DESCP = "name"; // description
        public static readonly String XML_ROOT = "root";
        public static readonly String XML_ENTITY = "entity";
        public static readonly String XML_CRITICAL_LEVEL = "critical"; // critical
        // level
        public static readonly String XML_BANK_KEY = "bank_key"; // bank key
        public static readonly String XML_BANK_ACCOUNT = "bank_account"; // bank
        public static readonly String XML_INIT_AMOUNT = "init_amount"; // init amount
        // account
        public static readonly String XML_TYPE = "type";


        private MasterDataUtils()
        {
        }
    }

    /// <summary>
    /// master data type
    /// </summary>
    public enum MasterDataType
    {
        VENDOR, CUSTOMER, BUSINESS_AREA, BANK_KEY, BANK_ACCOUNT, GL_ACCOUNT
    }

    /// <summary>
    /// master data managment
    /// </summary>
    public class MasterDataManagement : AbstractManagement
    {
        public static readonly String MASTER_DATA_FOLDER = "master_data";

        private readonly Dictionary<MasterDataType, MasterDataFactoryBase> _factoryList;

        /// <summary>
        /// balance accounts
        /// </summary>
        public List<GLAccountMasterData> BalanceAccounts
        {
            get
            {
                GLAccountMasterDataFactory factory = (GLAccountMasterDataFactory)
                    GetMasterDataFactory(MasterDataType.GL_ACCOUNT);
                return factory.BalanceAccounts;
            }
        }

        /// <summary>
        /// Liquidity accounts
        /// </summary>
        public List<GLAccountMasterData> LiquidityAccounts
        {
            get
            {
                GLAccountMasterDataFactory factory = (GLAccountMasterDataFactory)
                    GetMasterDataFactory(MasterDataType.GL_ACCOUNT);
                return factory.LiabilityAccounts;
            }
        }

        /// <summary>
        /// Liability accounts
        /// </summary>
        public List<GLAccountMasterData> LiabilityAccounts
        {
            get
            {
                GLAccountMasterDataFactory factory = (GLAccountMasterDataFactory)
                    GetMasterDataFactory(MasterDataType.GL_ACCOUNT);
                return factory.LiabilityAccounts;
            }
        }

        /// <summary>
        /// Cost accounts
        /// </summary>
        public List<GLAccountMasterData> CostAccounts
        {
            get
            {
                GLAccountMasterDataFactory factory = (GLAccountMasterDataFactory)
                    GetMasterDataFactory(MasterDataType.GL_ACCOUNT);
                return factory.CostAccounts;
            }
        }

        /// <summary>
        /// Revenue accounts
        /// </summary>
        public List<GLAccountMasterData> RevenueAccounts
        {
            get
            {
                GLAccountMasterDataFactory factory = (GLAccountMasterDataFactory)
                    GetMasterDataFactory(MasterDataType.GL_ACCOUNT);
                return factory.RevenueAccounts;
            }
        }

        /// <summary>
        /// Master data management
        /// </summary>
        /// <param name="coreDriver"></param>
        internal MasterDataManagement(CoreDriver coreDriver)
            : base(coreDriver)
        {
            _factoryList = new Dictionary<MasterDataType, MasterDataFactoryBase>();
            // initialize the hash table
        }


        /// <summary>
        /// parse XDocument to master data factory
        /// </summary>
        /// <param name="constructor"></param>
        /// <param name="type"></param>
        /// <param name="doc"></param>
        /// <returns></returns>
        /// <exception cref="MasterDataFileFormatException">Master data file format Exception</exception>
        /// <exception cref="SystemException">Bug</exception>
        private MasterDataFactoryBase factoryParser(
            MasterDataType type, XDocument doc)
        {
            _coreDriver.logDebugInfo(this.GetType(), 88,
                    String.Format("Parsing XML {0}...", type), MessageType.INFO);

            // get root element
            XElement rootElem = doc.Element(MasterDataUtils.XML_ROOT);
            if (rootElem == null)
            {
                _coreDriver.logDebugInfo(this.GetType(), 112,
                        String.Format("No root element for master data {0}", type), MessageType.ERRO);
                throw new MasterDataFileFormatException(type);
            }

            MasterDataFactoryBase newFactory;
            #region Create new factory with contructor
            switch (type)
            {
                case MasterDataType.BANK_ACCOUNT:
                    newFactory = new BankAccountMasterDataFactory(_coreDriver, this);
                    break;
                case MasterDataType.BANK_KEY:
                    newFactory = new BankKeyMasterDataFactory(_coreDriver, this);
                    break;
                case MasterDataType.BUSINESS_AREA:
                    newFactory = new BusinessAreaMasterDataFactory(_coreDriver, this);
                    break;
                case MasterDataType.CUSTOMER:
                    newFactory = new CustomerMasterDataFactory(_coreDriver, this);
                    break;
                case MasterDataType.GL_ACCOUNT:
                    newFactory = new GLAccountMasterDataFactory(_coreDriver, this);
                    break;
                case MasterDataType.VENDOR:
                    newFactory = new VendorMasterDataFactory(_coreDriver, this);
                    break;
                default:
                    throw new SystemException(new NoMasterDataFactoryClass(type));
            }

            #endregion

            try
            {
                foreach (XElement xelem in rootElem.Elements())
                {
                    if (xelem.Name.LocalName.Equals(MasterDataUtils.XML_ENTITY))
                    {
                        // parse master data entity
                        MasterDataBase masterData = newFactory.ParseMasterData(
                                _coreDriver, xelem);

                        // raise load master data
                        _coreDriver.ListenerMgmt.LoadMasterData(
                                this, masterData);
                    }
                }
            }
            catch (ArgumentNullException e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 133, e.Message,
                        MessageType.ERRO);
                throw new SystemException(e);
            }

            _coreDriver.logDebugInfo(this.GetType(), 88,
                    String.Format("Parsing XML {0} complete.", type),
                    MessageType.INFO);
            return newFactory;
        }

        /// <summary>
        /// load the data from file system
        /// </summary>
        /// <returns></returns>
        /// <exception cref="MasterDataFileFormatException"></exception>
        /// <exception cref="SystemException"></exception>
        public override async Task InitializeAsync()
        {
            _coreDriver.logDebugInfo(this.GetType(), 141,
                    "Master data loading...", MessageType.INFO);
            foreach (MasterDataType type in Enum.GetValues(typeof(MasterDataType)))
            {
                _coreDriver.logDebugInfo(this.GetType(), 144,
                        String.Format("Loading {0}...", type), MessageType.INFO);

                StorageFile file = await getMasterFileAsync(type);
                _coreDriver.logDebugInfo(this.GetType(), 148, String.Format(
                        "Master data file {0} for master data {1}.", file.Path, type),
                        MessageType.INFO);

                String str = await FileIO.ReadTextAsync(file);
                XDocument xdoc = XDocument.Parse(str);

                // parse
                MasterDataFactoryBase newFactory = factoryParser(type, xdoc);

                _coreDriver.logDebugInfo(this.GetType(), 208,
                        String.Format("Loading %s completed.", type),
                        MessageType.INFO);

                this._factoryList.Add(type, newFactory);
            }

            _coreDriver.logDebugInfo(
                    this.GetType(),
                    282,
                    "Master data loading completed.", MessageType.INFO);
        }

        /// <summary>
        /// Get master data factory
        /// </summary>
        /// <param name="type"></param>
        /// <returns></returns>
        public MasterDataFactoryBase GetMasterDataFactory(MasterDataType type)
        {
            if (!this._factoryList.ContainsKey(type))
            {
                return null;
            }
            MasterDataFactoryBase factory;
            this._factoryList.TryGetValue(type, out factory);
            return factory;
        }

        /// <summary>
        /// get master data with identity
        /// </summary>
        /// <param name="idStr"></param>
        /// <param name="type"></param>
        /// <returns></returns>
        /// <exception cref="IdentityInvalidChar"></exception>
        /// <exception cref="IdentityNoData"></exception>
        /// <exception cref="IdentityTooLong"></exception>
        public MasterDataBase GetMasterData(char[] idStr, MasterDataType type)
        {
            MasterDataFactoryBase factory = this.GetMasterDataFactory(type);
            MasterDataIdentity id = new MasterDataIdentity(idStr);

            return factory.GetEntity(id);
        }

        /// <summary>
        /// get master data 
        /// </summary>
        /// <param name="id"></param>
        /// <param name="type"></param>
        /// <returns></returns>
        public MasterDataBase GetMasterData(MasterDataIdentity id,
                MasterDataType type)
        {
            MasterDataFactoryBase factory = this.GetMasterDataFactory(type);
            return factory.GetEntity(id);
        }

        /// <summary>
        /// constain master data
        /// </summary>
        /// <param name="id"></param>
        /// <param name="type"></param>
        /// <returns></returns>
        public bool ContainsMasterData(MasterDataIdentity id, MasterDataType type)
        {
            MasterDataFactoryBase factory = this.GetMasterDataFactory(type);
            return factory.Contains(id);
        }

        /// <summary>
        /// Get GL Account based on G/L account group
        /// </summary>
        /// <param name="group"></param>
        /// <returns></returns>
        public List<MasterDataIdentity_GLAccount> GetGLAccountsBasedGroup(
                GLAccountGroupENUM group)
        {
            MasterDataFactoryBase factory = GetMasterDataFactory(MasterDataType.GL_ACCOUNT);
            List<MasterDataBase> datas = factory.AllEntities;
            List<MasterDataIdentity_GLAccount> array = new List<MasterDataIdentity_GLAccount>();

            foreach (MasterDataBase data in datas)
            {
                GLAccountMasterData glAccount = (GLAccountMasterData)data;
                if (glAccount.Group.Identity == group)
                {
                    array.Add(glAccount.GLIdentity);
                }
            }

            array.Sort();
            return array;
        }

        /// <summary>
        /// clear memory
        /// </summary>
        public override void Clear()
        {
            _factoryList.Clear();
        }

        /// <summary>
        /// establish master data files
        /// </summary>
        /// <returns></returns>
        public override async Task EstablishFilesAsync()
        {
            StorageFolder masterFolder = await _coreDriver.RootFolder.CreateFolderAsync(MASTER_DATA_FOLDER
                , CreationCollisionOption.OpenIfExists);

            // establish master data files
            foreach (MasterDataType type in Enum.GetValues(typeof(MasterDataType)))
            {
                MasterDataFactoryBase newFactory;
                #region Create new factory with contructor
                switch (type)
                {
                    case MasterDataType.BANK_ACCOUNT:
                        newFactory = new BankAccountMasterDataFactory(_coreDriver, this);
                        break;
                    case MasterDataType.BANK_KEY:
                        newFactory = new BankKeyMasterDataFactory(_coreDriver, this);
                        break;
                    case MasterDataType.BUSINESS_AREA:
                        newFactory = new BusinessAreaMasterDataFactory(_coreDriver, this);
                        break;
                    case MasterDataType.CUSTOMER:
                        newFactory = new CustomerMasterDataFactory(_coreDriver, this);
                        break;
                    case MasterDataType.GL_ACCOUNT:
                        newFactory = new GLAccountMasterDataFactory(_coreDriver, this);
                        break;
                    case MasterDataType.VENDOR:
                        newFactory = new VendorMasterDataFactory(_coreDriver, this);
                        break;
                    default:
                        throw new SystemException(new NoMasterDataFactoryClass(type));
                }
                _factoryList.Add(type, newFactory);
                #endregion

                XDocument xdoc = newFactory.ToXmlDocument();
                StorageFile file = await getMasterFileAsync(type);
                await FileIO.WriteTextAsync(file, xdoc.ToString());
            }
        }

        /// <summary>
        /// get master data file path
        /// </summary>
        /// <param name="type"></param>
        /// <returns></returns>
        private async Task<StorageFile> getMasterFileAsync(MasterDataType type)
        {
            String path = null;
            if (type == MasterDataType.VENDOR)
            {
                path = VendorMasterData.FILE_NAME;
            }
            else if (type == MasterDataType.CUSTOMER)
            {
                path = CustomerMasterData.FILE_NAME;
            }
            else if (type == MasterDataType.BUSINESS_AREA)
            {
                path = BusinessAreaMasterData.FILE_NAME;
            }
            else if (type == MasterDataType.BANK_KEY)
            {
                path = BankKeyMasterData.FILE_NAME;
            }
            else if (type == MasterDataType.BANK_ACCOUNT)
            {
                path = BankAccountMasterData.FILE_NAME;
            }
            else if (type == MasterDataType.GL_ACCOUNT)
            {
                path = GLAccountMasterData.FILE_NAME;
            }

            StorageFolder folder = await _coreDriver.RootFolder
                .GetFolderAsync(MASTER_DATA_FOLDER);
            if (folder == null)
            {
                return null;
            }
            return await folder.CreateFileAsync(path, CreationCollisionOption.OpenIfExists);
        }

        /// <summary>
        /// Store
        /// </summary>
        public async Task StoreAsync()
        {
            foreach (MasterDataType type in Enum.GetValues(typeof(MasterDataType)))
            {
                await StoreSingleAsync(type);
            }
        }

        /// <summary>
        /// store single master data
        /// </summary>
        /// <param name="type"></param>
        /// <exception cref="SystemException">Bug</exception>
        public async Task StoreSingleAsync(MasterDataType type)
        {
            _coreDriver.logDebugInfo(this.GetType(), 335,
                    String.Format("Starting to store master data {0}...", type),
                    MessageType.INFO);

            MasterDataFactoryBase factory;
            if (!_factoryList.TryGetValue(type, out factory))
            {
                throw new SystemException(new MasterDataFileFormatException(type));
            }

            if (factory._containDirtyData == false)
            {
                _coreDriver
                        .logDebugInfo(
                                this.GetType(),
                                335,
                                "No dirty data in the memory. No need to save the data to disk.",
                                MessageType.INFO);
                return;
            }
            // get XML document
            XDocument xdoc = factory.ToXmlDocument();
            StorageFile file = await this.getMasterFileAsync(type);
            try
            {
                await FileIO.WriteTextAsync(file, xdoc.ToString());
            }
            catch (FileNotFoundException e)
            {
                throw new SystemException(e);
            }

            factory._containDirtyData = false;
            _coreDriver.logDebugInfo(this.GetType(), 335,
                    String.Format("Store master data %s complete.", type),
                    MessageType.INFO);
        }
    }
}
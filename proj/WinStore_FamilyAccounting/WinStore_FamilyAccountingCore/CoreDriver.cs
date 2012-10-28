using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Storage;
using Windows.Storage.Search;
using WinStore_FamilyAccountingCore.Exceptions;
using WinStore_FamilyAccountingCore.Exceptions.FormatExceptions;
using WinStore_FamilyAccountingCore.Listeners;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Reports;
using WinStore_FamilyAccountingCore.Transaction;
using WinStore_FamilyAccountingCore.Utilities;

namespace WinStore_FamilyAccountingCore
{
    public class CoreDriver
    {
        /// <summary>
        /// version number
        /// </summary>
        public static readonly int AQUARIUS1_0 = 1;

        public static readonly String META_DATA = "metadata.txt";

        public static readonly String LOG_FILE = "log.txt";

        public static readonly String START_YEAR_TAG = "start_year";

        public static readonly String START_MONTH_TAG = "start_month";

        public static readonly String VERSION = "version";

        public static readonly String MASTERDATA = "MD";

        public static readonly String TRANDATA = "TD";

        public static readonly String REPORTDATA = "RD";

        private bool _flushLog2FileSystem = false;
        public bool FlushLog2FileSystem { get { return _flushLog2FileSystem; } set { _flushLog2FileSystem = value; } }

        private readonly ListenersManagement _listenerManagement;
        public ListenersManagement ListenerMgmt { get { return _listenerManagement; } }


        private MonthIdentity _startMonthId;
        /// <summary>
        /// current calendar month identity
        /// </summary>
        public MonthIdentity CurCalendarMonthId
        {
            get
        {
            MonthIdentity curMonthId;
            try
            {
                curMonthId = new MonthIdentity(DateTime.Today.Year,
                        DateTime.Today.Month);
                return curMonthId;
            }
            catch (FiscalYearRangeException e)
            {
                throw new SystemException(e);// bug
            }
            catch (FiscalMonthRangeException e)
            {
                throw new SystemException(e);// bug
            }
        }
        }
        /// <summary>
        /// month identities
        /// </summary>
        public MonthIdentity[] MonthIds
        {
            get
            {
                List<MonthIdentity> idArray = new List<MonthIdentity>();
                MonthIdentity curMonthId = this.CurCalendarMonthId;

                for (MonthIdentity monthId = _startMonthId; monthId
                        .CompareTo(curMonthId) <= 0; monthId = monthId.AddMonth())
                {
                    idArray.Add(monthId);
                }
                return idArray.ToArray<MonthIdentity>();
            }
        }
        public MonthIdentity StartMonthId
        {
            get { return _startMonthId; }
        }

        /// <summary>
        /// is initialized
        /// </summary>
        private bool _isInitialized;
        public bool IsInitialize { get { return _isInitialized; } }

        /// <summary>
        /// hash table, key string, value ManagementBase
        /// </summary>
        private readonly Dictionary<string, AbstractManagement> _managements;
        /// <summary>
        /// master data management
        /// </summary>
        public MasterDataManagement MdMgmt
        {
            get
            {
                if (this._isInitialized == false)
                    return null;
                return (MasterDataManagement)this.GetManagement(MASTERDATA);
            }
        }
        /// <summary>
        /// transaction data management
        /// </summary>
        public TransactionDataManagement TransMgmt
        {
            get
            {
                if (this._isInitialized == false)
                    return null;
                return (TransactionDataManagement)this.GetManagement(TRANDATA);
            }
        }
        /// <summary>
        /// reports management
        /// </summary>
        public ReportsManagement ReportsMgmt
        {
            get
            {
                if (this._isInitialized == false)
                    return null;
                return (ReportsManagement)this.GetManagement(REPORTDATA);
            }
        }

        private List<DebugInformation> _infos;
        public DebugInformation[] DebugInfo { get { return _infos.ToArray<DebugInformation>(); } }

        private Language _language;
        public Language Lang { get { return _language; } set { _language = value; } }

        /**
         * singleton
         */
        public CoreDriver()
        {
            _infos = new List<DebugInformation>();

            _listenerManagement = new ListenersManagement();
            // _listenerManagement.addCloseLedgerListener(_closeLedgerListener);

            // managements
            _managements = new Dictionary<String, AbstractManagement>();

            MasterDataManagement mdMgmt = new MasterDataManagement(this);
            _managements.Add(MASTERDATA, new MasterDataManagement(this));


            _managements.Add(TRANDATA, new TransactionDataManagement(this, mdMgmt));
            _managements.Add(REPORTDATA, new ReportsManagement(this, mdMgmt));

            _startMonthId = null;

            _isInitialized = false;
            _language = Language.SimpleChinese;
        }


        /// <summary>
        /// Log information for trouble track
        /// </summary>
        /// <param name="cl"></param>
        /// <param name="lineNum"></param>
        /// <param name="msg"></param>
        /// <param name="type"></param>
        public void logDebugInfo(Type cl, int lineNum, String msg,
                MessageType type)
        {
            DebugInformation debugInfo = new DebugInformation(cl, lineNum, msg,
                    type);
            _infos.Add(debugInfo);

            if (_flushLog2FileSystem == false)
            {
                return;
            }

            #region Append log to log file
            #endregion
        }

        /// <summary>
        /// Initialize. Before the core driver really works, it should initialize. In
        /// initializing process, system will set up the folder, master data folder
        /// and transaction folder. And then, transaction and master data management
        /// will initialize.
        /// </summary>
        private void init()
        {
            this.logDebugInfo(this.GetType(), 96, "Core driver initializing...",
                    MessageType.INFO);

            // check root folder
            AbstractManagement m1;
            m1 = GetManagement(MASTERDATA);
            m1.Initialize();
            m1 = GetManagement(TRANDATA);
            m1.Initialize();
            m1 = GetManagement(REPORTDATA);
            m1.Initialize();

            // initialize management
            foreach (var item in _managements)
            {
                if (item.Value is TransactionDataManagement
                        || item.Value is MasterDataManagement
                    || item.Value is ReportsManagement)
                {
                    continue;
                }

                item.Value.Initialize();
            }

            this.logDebugInfo(this.GetType(), 134,
                    "Core driver initializing completed.", MessageType.INFO);
        }

        /// <summary>
        /// Restart the core driver
        /// </summary>
        /// <exception cref="MetaDataFormatException">Format exception of meta-data file</exception>
        public async void RestartAsync()
        {
            Clear();

            // check root folder
            StorageFolder rootFolder = ApplicationData.Current.LocalFolder;
            StorageFile mdFile = await rootFolder.GetFileAsync(META_DATA);

            if (mdFile != null)
            {
                // load meta data
                try
                {
                    loadMetaData();
                    init();

                    _isInitialized = true;
                }
                catch (FormatException e)
                {
                    this.logDebugInfo(this.GetType(), 210, e.ToString(),
                            MessageType.ERRO);
                    // no handler
                    this.Clear();
                }
                return;
            }


            // folder not exist or folder is empty
            establishFolderAsync();
        }

        /// <summary>
        /// load meta data
        /// </summary>
        /// <exception cref="MetaDataFormatException">Format exception of the meta-data file</exception>
        private async void loadMetaData()
        {
            StorageFolder rootFolder = ApplicationData.Current.LocalFolder;
            StorageFile mdFile = await rootFolder.GetFileAsync(META_DATA);

            if (mdFile == null)
            {
                return;
            }

            try
            {
                IList<string> lines = await FileIO.ReadLinesAsync(mdFile);

                int startYear = 0;
                int startMonth = 0;
                int versionNumber = 0;
                foreach (string line in lines)
                {
                    String[] values = line.Split('=');
                    if (values.Length != 2)
                    {
                        throw new MetaDataFormatException("Meta data format error.");
                    }

                    if (values[0].Equals(START_MONTH_TAG))
                    {
                        startMonth = Int32.Parse(values[1]);
                    }
                    else if (values[0].Equals(START_YEAR_TAG))
                    {
                        startYear = Int32.Parse(values[1]);
                    }
                    else if (values[0].Equals(VERSION))
                    {
                        versionNumber = Int32.Parse(values[1]);
                    }
                }

                _startMonthId = new MonthIdentity(startYear, startMonth);
                // _curMonthId = new MonthIdentity(curYear, curMonth);
                // check version
                bool ret = this.versionCheck(versionNumber);
                if (ret == false)
                {
                    throw new MetaDataFormatException("Version Error");
                }
            }
            catch (ArgumentNullException e)
            {
                throw new MetaDataFormatException(e.Message);
            }
            catch (FormatException e)
            {
                throw new MetaDataFormatException(e.Message);
            }
            catch (OverflowException e)
            {
                throw new MetaDataFormatException(e.Message);
            }
            finally
            {
            }
        }

        /// <summary>
        /// save meta-data to file
        /// </summary>
        private async void saveMetaDataAsync()
        {
            List<string> lines = new List<string>();

            lines.Add(string.Format("{0}={1}\n", START_MONTH_TAG,
                    _startMonthId._fiscalMonth));
            lines.Add(string.Format("{0}={1}\n", START_YEAR_TAG,
                    _startMonthId._fiscalYear));
            // version
            lines.Add(string.Format("{0}={1}\n", VERSION, AQUARIUS1_0));

            // save meta data file
            StorageFolder rootFolder = ApplicationData.Current.LocalFolder;
            StorageFile mdFile = await rootFolder.CreateFileAsync(META_DATA, CreationCollisionOption.ReplaceExisting);

            await FileIO.WriteLinesAsync(mdFile, lines);
        }

        /// <summary>
        /// establish folder
        /// </summary>
        private void establishFolderAsync()
        {
            // set meta data
            int year = DateTime.Today.Year;
            int month = DateTime.Today.Month;

            // set starting month identity
            try
            {
                _startMonthId = new MonthIdentity(year, month);
                // _curMonthId = new MonthIdentity(year, month);
            }
            catch (FiscalYearRangeException e)
            {
                this.logDebugInfo(this.GetType(), 300, e.ToString(),
                        MessageType.ERRO);
                throw new SystemException(e);// bug
            }
            catch (FiscalMonthRangeException e)
            {
                this.logDebugInfo(this.GetType(), 302, e.ToString(),
                        MessageType.ERRO);
                throw new SystemException(e);// bug
            }

            saveMetaDataAsync();

            foreach (var item in _managements)
            {
                item.Value.EstablishFilesAsync();
            }
            _isInitialized = true;

        }

        /// <summary>
        /// Check version number
        /// </summary>
        /// <param name="version"></param>
        /// <returns></returns>
        private bool versionCheck(int version)
        {
            if (version == AQUARIUS1_0)
            {
                return true;
            }
            return false;
        }


        /// <summary>
        /// set starting month id
        /// </summary>
        /// <param name="monthId"></param>
        /// <returns></returns>
        public bool SetStartMonthId(MonthIdentity monthId)
        {
            // check
            if (this._startMonthId.CompareTo(monthId) <= 0)
            {
                return false;
            }

            this._startMonthId = monthId;

            // save meta data
            saveMetaDataAsync();

            return true;
        }


        /// <summary>
        /// Get the management
        /// </summary>
        /// <param name="str"></param>
        /// <returns></returns>
        public AbstractManagement GetManagement(String str)
        {
            if (_isInitialized == false)
            {
                return null;
            }
            AbstractManagement mgmt;
            _managements.TryGetValue(str, out mgmt);
            return mgmt;
        }



        /// <summary>
        /// clear data in memory, reset the driver core
        /// </summary>
        private void Clear()
        {
            foreach (var item in _managements)
            {
                item.Value.Clear();
            }
            _isInitialized = false;
        }

    }
}

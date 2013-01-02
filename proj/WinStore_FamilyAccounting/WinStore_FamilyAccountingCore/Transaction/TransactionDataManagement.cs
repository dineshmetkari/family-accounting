using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;
using Windows.Storage;
using WinStore_FamilyAccountingCore.Exceptions;
using WinStore_FamilyAccountingCore.Exceptions.FormatExceptions;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Utilities;

namespace WinStore_FamilyAccountingCore.Transaction
{
    public class TransactionDataManagement : AbstractManagement
    {

        public static readonly String TRANSACTION_DATA_FOLDER = "transaction_data";

        private readonly Dictionary<MonthIdentity, MonthLedger> _list;

        // private MonthLedger _openLedger; // current open ledger

        private readonly MasterDataManagement _masterDataMgmt;

        private readonly GLAccountBalanceCollection _glAccBalCol;
        public GLAccountBalanceCollection AccountBalanceCol { get { return _glAccBalCol; } }

        /// <summary>
        /// construct
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="mdMgmt"></param>
        public TransactionDataManagement(CoreDriver coreDriver, MasterDataManagement mdMgmt)
            : base(coreDriver)
        {
            _masterDataMgmt = mdMgmt;
            _list = new Dictionary<MonthIdentity, MonthLedger>();

            _glAccBalCol = new GLAccountBalanceCollection(_coreDriver,
                    _masterDataMgmt);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        /// <exception cref="TransactionDataFileFormatException"></exception>
        public override async Task InitializeAsync()
        {
            _coreDriver.logDebugInfo(this.GetType(), 75, String.Format(
                "Starting month identity is {0}", _coreDriver.StartMonthId
                        .ToString()), MessageType.INFO);

            MonthIdentity[] monthIdSet = _coreDriver.MonthIds;

            // loop all the month ledger file to load transaction data
            foreach (MonthIdentity monthId in monthIdSet)
            {
                _coreDriver.logDebugInfo(
                        this.GetType(),
                        75,
                        String.Format("loading month identity is {0}",
                                monthId.ToString()), MessageType.INFO);

                await Load(monthId);
            }
            // load current month identity
            // _openLedger = load(cureMonthId);

        }

        /// <summary>
        /// load month identity
        /// </summary>
        /// <param name="monthId"></param>
        /// <returns></returns>
        /// <exception cref="TransactionDataFileFormatException"></exception>
        public async Task<MonthLedger> Load(MonthIdentity monthId)
        {
            // construct month ledger
            MonthLedger monthledger = new MonthLedger(monthId);
            _list.Add(monthId, monthledger);

            _coreDriver.logDebugInfo(
                    this.GetType(),
                    102,
                    String.Format("Loading transaction data {0} ...",
                            monthId.ToString()), MessageType.INFO);

            // get file path
            StorageFile file = await generateFilePath(monthId, false);
            if (file == null)
            {// empty
                return monthledger;
            }
            _coreDriver.logDebugInfo(this.GetType(), 109,
                    String.Format("Transaction data file: {0} .", file.Path),
                    MessageType.INFO);
            try
            {
                string text = await FileIO.ReadTextAsync(file);
                XDocument xdoc = XDocument.Parse(text);

                XElement rootElem = xdoc.Element(TransDataUtils.XML_ROOT);
                // no root element
                if (rootElem == null)
                {
                    throw new TransactionDataFileFormatException(file.Path);
                }

                // -------------------------------------------------------------------
                // parse all the documents
                foreach (XElement elem in rootElem.Elements(TransDataUtils.XML_DOCUMENT))
                {
                    HeadEntity head = HeadEntity.Parse(_coreDriver,
                            _masterDataMgmt, elem);
                    _coreDriver
                            .logDebugInfo(
                                    this.GetType(),
                                    172,
                                    String.Format(
                                            "Document {0} add to list during loading.",
                                            head.DocIdentity
                                                    .ToString()),
                                    MessageType.INFO);
                    monthledger.Add(head);

                    // raise load document
                    _coreDriver.ListenerMgmt.LoadDoc(this, head);

                    // raise reverse document
                    if (head.IsReversed)
                        _coreDriver.ListenerMgmt.ReverseDoc(head);
                }
                // -----------------------------------------------------------------

                return monthledger;
            }
            catch (TransactionDataFileFormatException e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 170, e.Message,
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException(file.Path);
            }

        }

        /// <summary>
        /// get file 
        /// </summary>
        /// <param name="monthId"></param>
        /// <returns></returns>
        /// <exception cref="SystemException"></exception>
        private async Task<StorageFile> generateFilePath(MonthIdentity monthId, bool createIfNotExist)
        {
            StorageFolder transactionFolder = await _coreDriver.RootFolder
                .GetFolderAsync(TRANSACTION_DATA_FOLDER);
            if (transactionFolder == null)
            {
                throw new SystemException(null); // bug
            }
            String fileName = String.Format("{0}.xml", monthId.ToString());
            
            if (createIfNotExist == false)
            {
                try
                {
                    StorageFile file = await transactionFolder.GetFileAsync(fileName);
                    return file;
                }
                catch (FileNotFoundException)
                {
                    return null;
                }
            }
            else
            {
                StorageFile file = await transactionFolder.CreateFileAsync(fileName
                        , CreationCollisionOption.OpenIfExists);
                return file;
            }
        }

        /// <summary>
        /// save document
        /// </summary>
        /// <param name="head"></param>
        /// <param name="needStroe"></param>
        /// <exception cref="SystemException"></exception>
        /// <exception cref="SaveClosedLedgerException"></exception>
        internal async Task saveDocumentAsync(HeadEntity head, bool needStroe)
        {
            _coreDriver.logDebugInfo(this.GetType(), 231,
                    "Call transaction to save the document", MessageType.INFO);

            if (head.IsSaved)
            {
                _coreDriver.logDebugInfo(this.GetType(), 235,
                        "Document is saved, just to store the update on disk.",
                        MessageType.INFO);
            }
            else
            {
                _coreDriver.logDebugInfo(this.GetType(), 239,
                        "Document is never saved", MessageType.INFO);

                MonthIdentity monthId = head.MonthID;
                MonthLedger ledger = this.GetLedger(monthId);
                if (ledger == null)
                {
                    _coreDriver.logDebugInfo(this.GetType(), 239,
                            "Error in document month identity", MessageType.ERRO);
                    throw new SaveClosedLedgerException();
                }

                // set document number
                DocumentNumber num;
                List<HeadEntity> entities = ledger.Entities;
                if (entities.Count == 0)
                {
                    try
                    {
                        num = new DocumentNumber("1000000001".ToCharArray());
                    }
                    catch (IdentityTooLong e)
                    {
                        _coreDriver.logDebugInfo(this.GetType(), 239,
                                e.ToString(), MessageType.ERRO);
                        throw new SystemException(e);
                    }
                    catch (IdentityNoData e)
                    {
                        _coreDriver.logDebugInfo(this.GetType(), 239,
                                e.ToString(), MessageType.ERRO);
                        throw new SystemException(e);
                    }
                    catch (IdentityInvalidChar e)
                    {
                        _coreDriver.logDebugInfo(this.GetType(), 239,
                                e.ToString(), MessageType.ERRO);
                        throw new SystemException(e);
                    }
                }
                else
                {
                    HeadEntity last = entities[entities.Count - 1];
                    num = last.DocNumber.Next();
                }
                _coreDriver.logDebugInfo(this.GetType(), 239,
                        "Generate document number " + num.ToString(),
                        MessageType.INFO);
                head._docNumber = num;

                ledger.Add(head);
            }

            if (needStroe)
            {
                await StoreAsync(head.MonthID);
                _coreDriver.logDebugInfo(this.GetType(), 465,
                        "Memory has been stored to disk", MessageType.INFO);
            }
            else
            {
                _coreDriver.logDebugInfo(this.GetType(), 465,
                        "Memory has NOT been stored to disk", MessageType.INFO);
            }

            _coreDriver.logDebugInfo(this.GetType(), 278,
                    "Call transaction to save the document successfully",
                    MessageType.INFO);
        }

        /// <summary>
        /// store month ledger to file system
        /// </summary>
        /// <param name="monthId"></param>
        /// <exception cref="SystemException"></exception>
        public async Task StoreAsync(MonthIdentity monthId)
        {
            // store master data
            _coreDriver.logDebugInfo(this.GetType(), 293,
                    String.Format("Start storing %s to disk", monthId),
                    MessageType.INFO);

            _coreDriver.logDebugInfo(this.GetType(), 297,
                    "Store master data at first.", MessageType.INFO);
            await this._masterDataMgmt.StoreAsync();

            MonthLedger collection = this.GetLedger(monthId);

            StorageFile file = await this.generateFilePath(monthId, true);
            _coreDriver.logDebugInfo(this.GetType(), 297, "Generate file path: "
                    + file.Path, MessageType.INFO);
            if (file == null)
            {
                StorageFolder transactionFolder = await _coreDriver.RootFolder
                .GetFolderAsync(TRANSACTION_DATA_FOLDER);
                if (transactionFolder == null)
                {
                    throw new SystemException(null); // bug
                }
                String fileName = String.Format("{0}.xml", monthId.ToString());
                file = await transactionFolder.CreateFileAsync(fileName
                    , CreationCollisionOption.OpenIfExists);
            }


            XDocument xdoc = collection.toXML();
            _coreDriver
                    .logDebugInfo(this.GetType(), 297,
                            "Parsed document collections to XML document",
                            MessageType.INFO);
            try
            {
                await FileIO.WriteTextAsync(file, xdoc.ToString());
            }
            catch (FileNotFoundException e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 316, e.Message,
                        MessageType.ERRO);
                throw new SystemException(e);
            }

            _coreDriver.logDebugInfo(this.GetType(), 335,
                    "Save document collection successfully.", MessageType.INFO);
        }

        /// <summary>
        /// store month identity
        /// </summary>
        /// <param name="monthId"></param>
        public async void Store(MonthIdentity monthId)
        {
            await StoreAsync(monthId);
        }

        /// <summary>
        /// Get Ledger
        /// </summary>
        /// <param name="monthId"></param>
        /// <returns></returns>
        public MonthLedger GetLedger(MonthIdentity monthId)
        {
            // get current calendar month
            MonthIdentity curMonthId = _coreDriver.CurCalendarMonthId;

            // check whether ledger beyond the range
            if (_coreDriver.StartMonthId.CompareTo(monthId) > 0
                    || curMonthId.CompareTo(monthId) < 0)
            {
                return null;
            }

            // create new month ledger
            MonthLedger monthLedger;
            if (!_list.TryGetValue(monthId, out monthLedger))
            {
                monthLedger = new MonthLedger(monthId);
                _list.Add(monthId, monthLedger);
            }

            return monthLedger;
        }


        /// <summary>
        /// get ledger
        /// </summary>
        /// <param name="fiscalYear"></param>
        /// <param name="fiscalMonth"></param>
        /// <returns></returns>
        public MonthLedger GetLedger(int fiscalYear, int fiscalMonth)
        {
            MonthIdentity monthId;
            try
            {
                monthId = new MonthIdentity(fiscalYear, fiscalMonth);
            }
            catch (FiscalYearRangeException)
            {
                return null;
            }
            catch (FiscalMonthRangeException)
            {
                return null;
            }

            return GetLedger(monthId);
        }

        /// <summary>
        /// Reverse Document
        /// </summary>
        /// <param name="docId"></param>
        /// <exception cref="SystemException"></exception>
        /// <exception cref="ReverseOrgDocNotExistException"></exception>
        /// <exception cref="DocReservedException"></exception>
        public void ReverseDocument(DocumentIdentity docId)
        {
            _coreDriver.logDebugInfo(this.GetType(), 348,
                    "Start reversing document " + docId.ToString(),
                    MessageType.INFO);

            // ------------------------------------------
            // check document
            HeadEntity orgHead = this.GetEntity(docId);
            if (orgHead == null)
            {
                _coreDriver.logDebugInfo(this.GetType(), 348, "No such document",
                        MessageType.ERRO);
                throw new ReverseOrgDocNotExistException();
            }
            if (orgHead.IsReversed)
            {
                _coreDriver.logDebugInfo(this.GetType(), 348,
                        "Document has been reserved before", MessageType.ERRO);
                throw new DocReservedException();
            }

            _coreDriver.logDebugInfo(this.GetType(), 399,
                    "Update reverse document information", MessageType.INFO);
            // update reverse information
            orgHead._isReversed = true;

            // raise event to update balance
            _coreDriver.ListenerMgmt.ReverseDoc(orgHead);

            _coreDriver.logDebugInfo(this.GetType(), 399,
                    "Store memory to disk during reverse document",
                    MessageType.INFO);

            // store to file system
            this.Store(orgHead.MonthID);


            String info = String.Format("Document {0} is reversed.",
                    orgHead.DocNumber);

            _coreDriver.logDebugInfo(this.GetType(), 416, info, MessageType.INFO);

        }


        /// <summary>
        /// get entity
        /// </summary>
        /// <param name="docId"></param>
        /// <returns></returns>
        public HeadEntity GetEntity(DocumentIdentity docId)
        {
            MonthLedger ledger = this.GetLedger(docId._monthIdentity);
            if (ledger == null)
            {
                return null;
            }
            return ledger.GetEntity(docId);
        }

        /// <summary>
        /// clear
        /// </summary>
        public override void Clear()
        {
            _list.Clear();
        }

        /// <summary>
        /// establish files
        /// </summary>
        /// <returns></returns>
        public override async Task EstablishFilesAsync()
        {
            // set up transaction data folder
            await _coreDriver.RootFolder.CreateFolderAsync(TRANSACTION_DATA_FOLDER
                , CreationCollisionOption.OpenIfExists);
            _coreDriver
                    .logDebugInfo(
                            this.GetType(),
                            125,
                            "Create transaction data root folder.",
                            MessageType.INFO);

            foreach (MonthIdentity monthId in _coreDriver.MonthIds)
            {
                // create transaction file for each available month ledger
                MonthLedger ledger = new MonthLedger(monthId);
                _list.Add(monthId, ledger);
                XDocument xdoc = ledger.toXML();

                StorageFile file = await generateFilePath(monthId, true);
                try
                {
                    await FileIO.WriteTextAsync(file, xdoc.ToString());
                }
                catch (FileNotFoundException e)
                {
                    _coreDriver.logDebugInfo(this.GetType(), 530, e.Message,
                            MessageType.ERRO);
                    throw new SystemException(e);
                }
            }
        }

        public override bool NeedInit
        {
            get { return true; }
        }

        public override bool NeedEstablishFile
        {
            get { return true; }
        }
    }
}

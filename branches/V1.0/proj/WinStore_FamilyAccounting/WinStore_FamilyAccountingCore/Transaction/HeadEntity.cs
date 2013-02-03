using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;
using WinStore_FamilyAccountingCore.Exceptions;
using WinStore_FamilyAccountingCore.Exceptions.FormatExceptions;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Utilities;

namespace WinStore_FamilyAccountingCore.Transaction
{
    public enum DocumentType
    {
        GL = ('S'), VENDOR_INVOICE = ('K'), CUSTOMER_INVOICE = ('D')
    }

    /// <summary>
    /// Head entity
    /// </summary>
    public class HeadEntity : IComparable<HeadEntity>
    {

        /// <summary>
        /// the document number generated when saving
        /// </summary>
        internal DocumentNumber _docNumber;
        public DocumentNumber DocNumber { get { return _docNumber; } }
        public DocumentIdentity DocIdentity
        {
            get
            {
                if (_docNumber == null || _monthId == null)
                {
                    return null;
                }
                return new DocumentIdentity(_docNumber, _monthId);
            }
        }

        private MonthIdentity _monthId;
        public MonthIdentity MonthID { get { return _monthId; } }
        public int FiscalYear { get { return _monthId.FiscalYear; } }
        public int FiscalMonth { get { return _monthId.FiscalMonth; } }

        private DateTime _postingDate;
        public DateTime PstDate { get { return _postingDate; } }

        private String _docText;
        public String DocText { get { return _docText; } }

        private DocumentType _type;
        public DocumentType DocType { get { return _type; } }

        internal bool _isReversed;
        public bool IsReversed { get { return _isReversed; } }

        public readonly CoreDriver _coreDriver;

        public readonly MasterDataManagement _management;

        private readonly List<ItemEntity> _items;
        public List<ItemEntity> Items
        {
            get
            {
                List<ItemEntity> ret = new List<ItemEntity>(_items);
                ret.Sort();
                return ret;
            }
        }

        private bool _isSaved;
        public bool IsSaved { get { return _isSaved; } }

        /// <summary>
        /// fields
        /// </summary>
        private Dictionary<String, String> _fields;

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="management"></param>
        public HeadEntity(CoreDriver coreDriver, MasterDataManagement management)
        {
            _coreDriver = coreDriver;
            _management = management;
            _items = new List<ItemEntity>();
            _fields = new Dictionary<String, String>();

            _postingDate = DateTime.Today;
            _docText = String.Empty;

            _isReversed = false;
            _isSaved = false;
        }

        /// <summary>
        /// set posting date
        /// </summary>
        /// <param name="postingDate"></param>
        /// <returns></returns>
        /// <exception cref="SystemException"></exception>
        public bool setPostingDate(DateTime postingDate)
        {
            if (_isSaved)
            {
                return false;
            }
            _postingDate = postingDate;
            try
            {
                _monthId = new MonthIdentity(postingDate.Year, postingDate.Month);
            }
            catch (FiscalYearRangeException e)
            {
                throw new SystemException(e);
            }
            catch (FiscalMonthRangeException e)
            {
                throw new SystemException(e);
            }

            return true;
        }

        /// <summary>
        /// set document text
        /// </summary>
        /// <param name="text"></param>
        public void SetDocText(String text)
        {
            _docText = text;
        }

        /// <summary>
        /// set document type
        /// </summary>
        /// <param name="type"></param>
        /// <returns></returns>
        public bool SetDocumentType(DocumentType type)
        {
            if (_isSaved)
            {
                return false;
            }
            _type = type;
            return true;
        }

        /// <summary>
        /// Document items
        /// </summary>
        public List<ItemEntity> DocItems
        {
            get
            {
                List<ItemEntity> ret = new List<ItemEntity>(_items);
                ret.Sort();
                return ret;
            }
        }

        /// <summary>
        /// Item count
        /// </summary>
        public int ItemCount { get { return _items.Count; } }

        /// <summary>
        /// Add fields
        /// </summary>
        /// <param name="key"></param>
        /// <param name="value"></param>
        public void AddField(String key, String value)
        {
            _fields.Add(key, value);
        }

        /// <summary>
        /// get fields
        /// </summary>
        /// <param name="key"></param>
        /// <returns></returns>
        public String GetField(String key)
        {
            if (TransDataUtils.XML_DOC_NUM.Equals(key))
            {
                // document number
                return _docNumber.ToString();
            }
            else if (TransDataUtils.XML_YEAR.Equals(key))
            {
                // fiscal year
                return _monthId.FiscalYear.ToString();
            }
            else if (TransDataUtils.XML_MONTH.Equals(key))
            {
                // fiscal month
                return _monthId.FiscalMonth.ToString();
            }
            else if (TransDataUtils.XML_DATE.Equals(key))
            {
                // posting date
                return _postingDate.ToString(MasterDataUtils.DATE_FORMAT);
            }
            else if (TransDataUtils.XML_TEXT.Equals(key))
            {
                // doc text
                return _docText;
            }
            else if (TransDataUtils.XML_DOC_TYPE.Equals(key))
            {
                // doc type
                return ((char)_type).ToString();
            }
            else if (TransDataUtils.XML_IS_REVERSED.Equals(key))
            {
                // is reversed
                return _isReversed.ToString();
            }

            if (_fields.ContainsKey(key))
            {
                return null;
            }
            String ret;
            _fields.TryGetValue(key, out ret);
            return ret;
        }

        /// <summary>
        /// Fields
        /// </summary>
        /// <returns></returns>
        public List<String> Fields
        {
            get
            {
                List<String> fields = new List<String>();

                foreach (String str in TransDataUtils.HEAD_XML_TAGS)
                {
                    fields.Add(str);
                }

                fields.AddRange(_fields.Values);
                return fields;
            }
        }

        /// <summary>
        /// create new entity
        /// </summary>
        /// <returns></returns>
        public ItemEntity CreateEntity()
        {
            if (_isSaved)
            {
                return null;
            }

            int lineNum = _items.Count;
            ItemEntity item = new ItemEntity(_coreDriver, _management, this,
                    lineNum);
            _items.Add(item);

            return item;
        }

        /// <summary>
        /// To string
        /// </summary>
        /// <returns></returns>
        public override String ToString()
        {
            return DocIdentity.ToString();
        }

        /// <summary>
        /// Parse XElement to header
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="management"></param>
        /// <param name="elem"></param>
        /// <returns></returns>
        /// <exception cref="TransactionDataFileFormatException"></exception>
        public static HeadEntity Parse(CoreDriver coreDriver,
                MasterDataManagement management, XElement elem)
        {
            HeadEntity head = new HeadEntity(coreDriver, management);
            head._isSaved = true;

            #region get document number
            XAttribute docNumStr = elem.Attribute(TransDataUtils.XML_DOC_NUM);
            if (docNumStr == null)
            {
                coreDriver.logDebugInfo(typeof(HeadEntity), 271, String.Format(
                        "Field {0} is missing in.", TransDataUtils.XML_DOC_NUM),
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            try
            {
                head._docNumber = new DocumentNumber(docNumStr.Value.ToCharArray());
            }
            catch (Exception)
            {
                coreDriver.logDebugInfo(typeof(HeadEntity), 271, String.Format(
                        "Format of field {0} is error.", TransDataUtils.XML_DOC_NUM),
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            #endregion

            #region fiscal year
            XAttribute yearStr = elem.Attribute(TransDataUtils.XML_YEAR);
            if (yearStr == null)
            {
                coreDriver.logDebugInfo(typeof(HeadEntity), 283, String.Format(
                        "Field %s is missing in.", TransDataUtils.XML_YEAR),
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            int year;
            if (!Int32.TryParse(yearStr.Value, out year))
            {
                coreDriver.logDebugInfo(typeof(HeadEntity), 271, String.Format(
                        "Format of field {0} is error.", TransDataUtils.XML_YEAR),
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            #endregion

            #region fiscal month
            XAttribute monthStr = elem.Attribute(TransDataUtils.XML_MONTH);
            if (monthStr == null)
            {
                coreDriver.logDebugInfo(typeof(HeadEntity), 295, String.Format(
                        "Field %s is missing in.", TransDataUtils.XML_MONTH),
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            int month;
            if (!Int32.TryParse(monthStr.Value, out month))
            {
                coreDriver.logDebugInfo(typeof(HeadEntity), 271, String.Format(
                        "Format of field {0} is error.", TransDataUtils.XML_MONTH),
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            #endregion
            try
            {
                head._monthId = new MonthIdentity(year, month);
            }
            catch (FiscalMonthRangeException)
            {
                coreDriver.logDebugInfo(typeof(HeadEntity), 271, String.Format(
                        "Format of field {0} is error.", TransDataUtils.XML_MONTH),
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            catch (FiscalYearRangeException)
            {
                coreDriver.logDebugInfo(typeof(HeadEntity), 271, String.Format(
                        "Format of field {0} is error.", TransDataUtils.XML_YEAR),
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }

            // posting date
            XAttribute dateStr = elem.Attribute(TransDataUtils.XML_DATE);
            if (dateStr == null)
            {
                coreDriver.logDebugInfo(typeof(HeadEntity), 307, String.Format(
                        "Field %s is missing in.", TransDataUtils.XML_DATE),
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            if (!DateTime.TryParse(dateStr.Value, out head._postingDate))
            {
                coreDriver.logDebugInfo(typeof(HeadEntity), 271, String.Format(
                        "Format of field {0} is error.", TransDataUtils.XML_DATE),
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }

            // text
            XAttribute text = elem.Attribute(TransDataUtils.XML_TEXT);
            if (text == null)
            {
                coreDriver.logDebugInfo(typeof(HeadEntity), 325, String.Format(
                        "Field %s is missing in.", TransDataUtils.XML_TEXT),
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            head._docText = text.Value;

            // document type
            XAttribute docTypeStr = elem.Attribute(TransDataUtils.XML_DOC_TYPE);
            if (docTypeStr == null)
            {
                coreDriver.logDebugInfo(typeof(HeadEntity), 325, String.Format(
                        "Field %s is missing in.", TransDataUtils.XML_DOC_TYPE),
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            if (docTypeStr.Value.Length != 1
                || (docTypeStr.Value[0] != (char)DocumentType.CUSTOMER_INVOICE
                && docTypeStr.Value[0] != (char)DocumentType.GL
                && docTypeStr.Value[0] != (char)DocumentType.VENDOR_INVOICE))
            {
                coreDriver.logDebugInfo(typeof(HeadEntity), 271, String.Format(
                        "Format of field {0} is error.", TransDataUtils.XML_DOC_TYPE),
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            head._type = (DocumentType)docTypeStr.Value[0];


            // is reversed
            XAttribute isReversedStr = elem
                    .Attribute(TransDataUtils.XML_IS_REVERSED);
            if (isReversedStr == null)
            {
                coreDriver.logDebugInfo(typeof(HeadEntity), 338, String.Format(
                        "Field %s is missing in.", TransDataUtils.XML_IS_REVERSED),
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            if (!bool.TryParse(isReversedStr.Value, out head._isReversed))
            {
                coreDriver.logDebugInfo(typeof(HeadEntity), 271, String.Format(
                        "Format of field {0} is error.", TransDataUtils.XML_IS_REVERSED),
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }

            // parse item
            foreach (XElement itemElem in elem.Elements(TransDataUtils.XML_ITEM))
            {
                ItemEntity item = ItemEntity.Parse(coreDriver,
                        management, head, itemElem);
                item._isSaved = true;

                coreDriver
                        .logDebugInfo(
                                typeof(HeadEntity),
                                377,
                                String.Format(
                                        "Line Item %d appended during parsing document.",
                                        item.LineNum),
                                MessageType.INFO);
                head._items.Add(item);
            }

            // addition attributes
            foreach (XAttribute attr in elem.Attributes())
            {
                head._fields.Add(attr.Name.LocalName, attr.Value);
            }

            // remove fields is not additional fields
            foreach (String str in TransDataUtils.HEAD_XML_TAGS)
            {
                head._fields.Remove(str);
            }

            // check balance
            CurrencyAmount sum = new CurrencyAmount();
            foreach (ItemEntity item in head._items)
            {
                if (item.CdIndicator == CreditDebitIndicator.DEBIT)
                {
                    sum.AddTo(item.Amount);
                }
                else
                {
                    sum.MinusTo(item.Amount);
                }
            }

            if (sum.IsZero() == false)
            {
                throw new TransactionDataFileFormatException("No Balance");
            }

            StringBuilder strBuilder = new StringBuilder(
                    String.Format(
                            "Parse document %s with posting date %s, text %s, type %s, is_reversed %s",
                            head.DocIdentity, head.PstDate,
                            head.DocText, head.DocType,
                            head.IsReversed));
            coreDriver.logDebugInfo(typeof(HeadEntity), 377,
                    strBuilder.ToString(), MessageType.INFO);
            return head;
        }

        /// <summary>
        /// Compare to
        /// </summary>
        /// <param name="head"></param>
        /// <returns></returns>
        public int CompareTo(HeadEntity head)
        {
            int ret = this._docNumber.CompareTo(head._docNumber);
            return ret;
        }

        /// <summary>
        /// save document
        /// </summary>
        /// <param name="needStore"></param>
        /// <exception cref="SystemException"></exception>
        /// <exception cref="SaveClosedLedgerException"></exception>
        public async Task SaveAsync(bool needStore)
        {
            _coreDriver.logDebugInfo(this.GetType(), 427,
                    "Starting to save document...", MessageType.INFO);

            // check before save
            CheckBeforeSave();

            TransactionDataManagement transaction = _coreDriver.TransMgmt;

            _coreDriver
                    .logDebugInfo(
                            this.GetType(),
                            450,
                            "Check is OK. Then get the transaction management and then all the transaction to save the document.",
                            MessageType.INFO);

            // store
            await transaction.saveDocumentAsync(this, needStore);

            String info = String.Format("Document %s in %s saved successfully.",
                    _docNumber, _monthId);
            _coreDriver.logDebugInfo(this.GetType(), 459, info, MessageType.INFO);
            _isSaved = true;

            // raise saved document
            _coreDriver.ListenerMgmt.SaveDoc(this);
        }

        /// <summary>
        /// check before document is save
        /// </summary>
        /// <exception cref="MandatoryFieldIsMissing"></exception>
        /// <exception cref="BalanceNotZero"></exception>
        public void CheckBeforeSave()
        {
            // check posting date
            if (_postingDate == null)
            {
                _coreDriver.logDebugInfo(this.GetType(), 478,
                        "Check document before save, posting date is null",
                        MessageType.ERRO);
                throw new MandatoryFieldIsMissing("Posting Date");
            }

            CurrencyAmount sum = new CurrencyAmount();
            foreach (ItemEntity item in _items)
            {
                item.CheckMandatory();

                if (item.CdIndicator == CreditDebitIndicator.CREDIT)
                {
                    sum.MinusTo(item.Amount);
                }
                else if (item.CdIndicator == CreditDebitIndicator.DEBIT)
                {
                    sum.AddTo(item.Amount);
                }
            }

            // check balance
            if (!sum.IsZero())
            {
                _coreDriver.logDebugInfo(this.GetType(), 478,
                        "Check document before save, balance is not zero",
                        MessageType.ERRO);
                throw new BalanceNotZero();
            }

            _coreDriver.logDebugInfo(this.GetType(), 319,
                    "Check document before save successfully", MessageType.INFO);
        }

        /// <summary>
        /// parse document to XML
        /// </summary>
        /// <returns></returns>
        public XElement ToXml()
        {
            XElement xelem = new XElement(TransDataUtils.XML_DOCUMENT);

            // add fields XML name and value
            foreach (String str in TransDataUtils.HEAD_XML_TAGS)
            {
                String value = this.GetField(str);
                if (value != null)
                {
                    xelem.Add(new XAttribute(str, value));
                }
            }

            // items
            foreach (ItemEntity item in _items)
            {
                xelem.Add(item.ToXml());
            }
            return xelem;
        }
    }
}

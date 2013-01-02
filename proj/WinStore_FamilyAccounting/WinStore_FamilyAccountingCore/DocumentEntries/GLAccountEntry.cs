using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.Exceptions;
using WinStore_FamilyAccountingCore.Exceptions.FormatExceptions;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Transaction;
using WinStore_FamilyAccountingCore.Utilities;

namespace WinStore_FamilyAccountingCore.DocumentEntries
{
    public class GLAccountEntry : IDocumentEntry
    {

        public static readonly String SRC_ACCOUNT = "SOURCE_ACCOUNT";

        public static readonly String DST_ACCOUNT = "DESTINATION_ACCOUNT";

        private readonly CoreDriver _coreDriver;
        private readonly MasterDataManagement _mdMgmt;

        private HeadEntity _doc = null;

        private MasterDataIdentity_GLAccount _srcAccount; // source account

        private MasterDataIdentity_GLAccount _dstAccount; // destination account

        private DateTime _pstDate; // posting date

        private CurrencyAmount _amount;// amount

        private String _text; // document text

        private bool _isSaved;

        /// <summary>
        /// 
        /// constructor
        /// </summary>
        /// <param name="coreDriver"></param>
        public GLAccountEntry(CoreDriver coreDriver
            , MasterDataManagement mdMgmt)
        {
            _coreDriver = coreDriver;
            _mdMgmt = mdMgmt;
            _isSaved = false;
        }

        /// <summary>
        /// set source account
        /// </summary>
        /// <param name="fieldName"></param>
        /// <param name="value"></param>
        /// <exception cref="NoFieldNameException">No such field name</exception>
        /// <exception cref="NotInValueRangeException">The value is not supported</exception>
        private void setSourceAccount(MasterDataIdentity_GLAccount srcAccount)
        {
            if (srcAccount == null)
            {
                throw new NotInValueRangeException(SRC_ACCOUNT, "");
            }

            List<MasterDataBase> valueSet = this.GetValueSet(SRC_ACCOUNT);
            foreach (Object obj in valueSet)
            {
                GLAccountMasterData glAccount = (GLAccountMasterData)obj;
                if (glAccount.GLIdentity.Equals(srcAccount))
                {
                    _srcAccount = srcAccount;
                    return;
                }
            }

            throw new NotInValueRangeException(SRC_ACCOUNT, srcAccount);
        }

        /// <summary>
        /// set dst account
        /// </summary>
        /// <param name="fieldName"></param>
        /// <param name="value"></param>
        /// <exception cref="NoFieldNameException">No such field name</exception>
        /// <exception cref="NotInValueRangeException">The value is not supported</exception>
        private void setDstAccount(MasterDataIdentity_GLAccount dstAccount)
        {
            if (dstAccount == null)
            {
                throw new NotInValueRangeException(DST_ACCOUNT, "");
            }

            List<MasterDataBase> valueSet = this.GetValueSet(DST_ACCOUNT);
            foreach (Object obj in valueSet)
            {
                GLAccountMasterData glAccount = (GLAccountMasterData)obj;
                if (glAccount.GLIdentity.Equals(dstAccount))
                {
                    _dstAccount = dstAccount;
                    return;
                }
            }
            throw new NotInValueRangeException(SRC_ACCOUNT, dstAccount);
        }

        /// <summary>
        /// Check the document entry before saving
        /// </summary>
        /// <exception cref="MandatoryFieldIsMissing"></exception>
        public void CheckBeforeSave()
        {
            if (_srcAccount == null)
            {
                throw new MandatoryFieldIsMissing(SRC_ACCOUNT);
            }

            if (_dstAccount == null)
            {
                throw new MandatoryFieldIsMissing(DST_ACCOUNT);
            }

            if (_amount.IsNegative() || _amount.IsZero())
            {
                throw new MandatoryFieldIsMissing(EntryTemplate.AMOUNT);
            }

            if (_pstDate == null)
            {
                throw new MandatoryFieldIsMissing(EntryTemplate.POSTING_DATE);
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="store"></param>
        /// <exception cref="MandatoryFieldIsMissing"></exception>
        /// <exception cref="SaveClosedLedgerException"></exception>
        public async Task SaveAsync(bool store)
        {
            if (_isSaved)
            {
                return;
            }

            CheckBeforeSave();
            try
            {
                HeadEntity head = new HeadEntity(_coreDriver,
                        _coreDriver.MdMgmt);
                head.setPostingDate(_pstDate);
                head.SetDocText(_text);
                head.SetDocumentType(DocumentType.GL);

                ItemEntity srcItem = head.CreateEntity();
                srcItem.SetAmount(CreditDebitIndicator.CREDIT, _amount);
                srcItem.SetGLAccount(_srcAccount);

                ItemEntity dstItem = head.CreateEntity();
                dstItem.SetAmount(CreditDebitIndicator.DEBIT, _amount);
                dstItem.SetGLAccount(_dstAccount);


                await head.SaveAsync(store);


                _isSaved = true;
                _doc = head;

            }
            catch (ArgumentNullException e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 123, e.ToString(),
                        MessageType.ERRO);
                throw new SystemException(e);
            }
            catch (MasterDataIdentityNotDefined e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 123, e.ToString(),
                        MessageType.ERRO);
                throw new SystemException(e);
            }
            catch (BalanceNotZero)
            {
                _coreDriver.logDebugInfo(this.GetType(), 163,
                        "Balance is not zero", MessageType.ERRO);
            }

        }

        public HeadEntity getDocument()
        {
            if (_isSaved)
            {
                return _doc;
            }
            return null;
        }

        /// <summary>
        /// set value
        /// </summary>
        /// <param name="fieldName"></param>
        /// <param name="value"></param>
        /// <exception cref="NoFieldNameException">No such field name</exception>
        /// <exception cref="NotInValueRangeException">The value is not supported</exception>
        public void SetValue(String fieldName, Object value)
        {
            if (_isSaved)
            {
                return;
            }

            if (value == null)
            {
                throw new NotInValueRangeException(fieldName, "");
            }

            if (fieldName.Equals(DST_ACCOUNT))
            {
                if (!(value is MasterDataIdentity_GLAccount))
                {
                    throw new NotInValueRangeException(fieldName, value);
                }
                MasterDataIdentity_GLAccount id = (MasterDataIdentity_GLAccount)value;
                setDstAccount(id);
            }
            else if (fieldName.Equals(SRC_ACCOUNT))
            {
                if (!(value is MasterDataIdentity_GLAccount))
                {
                    throw new NotInValueRangeException(fieldName, value);
                }
                MasterDataIdentity_GLAccount id = (MasterDataIdentity_GLAccount)value;
                setSourceAccount(id);
            }
            else if (fieldName.Equals(EntryTemplate.TEXT))
            {
                _text = value.ToString();
            }
            else if (fieldName.Equals(EntryTemplate.AMOUNT))
            {
                try
                {
                    CurrencyAmount amount = CurrencyAmount.Parse(value.ToString());
                    if (amount.IsZero() || amount.IsNegative())
                    {
                        throw new NotInValueRangeException(fieldName, value);
                    }
                    _amount = amount;
                }
                catch (CurrencyAmountFormatException)
                {
                    throw new NotInValueRangeException(fieldName, value);
                }
            }
            else if (fieldName.Equals(EntryTemplate.POSTING_DATE))
            {
                if (!(value is DateTime))
                {
                    throw new NotInValueRangeException(fieldName, value);
                }
                DateTime pstDate = (DateTime)value;
                _pstDate = pstDate;
            }
            else
            {
                throw new NoFieldNameException(fieldName);
            }
        }

        // <summary>
        /// 
        /// </summary>
        /// <param name="fieldName"></param>
        /// <returns></returns>
        public Object GetValue(String fieldName)
        {
            if (fieldName.Equals(DST_ACCOUNT))
            {
                return _dstAccount;
            }
            else if (fieldName.Equals(SRC_ACCOUNT))
            {
                return _srcAccount;
            }
            else if (fieldName.Equals(EntryTemplate.TEXT))
            {
                return _text;
            }
            else if (fieldName.Equals(EntryTemplate.AMOUNT))
            {
                if (_amount == null || _amount.IsZero())
                {
                    return null;
                }
                return new CurrencyAmount(_amount);
            }
            else if (fieldName.Equals(EntryTemplate.POSTING_DATE))
            {
                return _pstDate;
            }
            throw new NoFieldNameException(fieldName);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="fieldName"></param>
        /// <returns></returns>
        /// <exception cref="NoFieldNameException"> no such field name</exception>
        public Object GetDefaultValue(String fieldName)
        {
            if (fieldName.Equals(DST_ACCOUNT))
            {
                return null;
            }
            else if (fieldName.Equals(SRC_ACCOUNT))
            {
                return null;
            }
            else if (fieldName.Equals(EntryTemplate.TEXT))
            {
                return "";
            }
            else if (fieldName.Equals(EntryTemplate.AMOUNT))
            {
                return 0;
            }
            throw new NoFieldNameException(fieldName);
        }

        public bool isSaved()
        {
            return _isSaved;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="fieldName"></param>
        /// <returns></returns>
        /// <exception cref="NoFieldNameException"> no such field name</exception>
        public List<MasterDataBase> GetValueSet(String fieldName)
        {
            if (fieldName.Equals(DST_ACCOUNT))
            {
                return _mdMgmt.LiquidityAccounts.ToList<MasterDataBase>();
            }
            else if (fieldName.Equals(SRC_ACCOUNT))
            {
                return _mdMgmt.LiquidityAccounts.ToList<MasterDataBase>();
            }
            throw new NoFieldNameException(fieldName);
        }

        /// <summary>
        /// Parse from head to entry
        /// </summary>
        /// <param name="head"></param>
        /// <returns>return null if parse error</returns>
        public static GLAccountEntry parse(HeadEntity head)
        {
            // check
            if (head.DocType != DocumentType.GL)
            {
                return null;
            }
            List<ItemEntity> items = head.Items;
            if (items.Count != 2)
            {
                return null;
            }
            // credit item
            ItemEntity srcItem = items[0];
            if (srcItem.AccType != AccountType.GL_ACCOUNT)
            {
                return null;
            }
            ItemEntity dstItem = items[1];
            if (dstItem.AccType != AccountType.GL_ACCOUNT)
            {
                return null;
            }

            GLAccountEntry entry = new GLAccountEntry(head._coreDriver
                , head._coreDriver.MdMgmt);
            entry._srcAccount = srcItem.GLAccount;
            entry._dstAccount = dstItem.GLAccount;
            entry._pstDate = head.PstDate;
            entry._amount = srcItem.Amount;
            if (srcItem.CdIndicator == CreditDebitIndicator.DEBIT)
            {
                // reverse
                entry._amount.Negate();
            }
            entry._text = head.DocText;
            entry._isSaved = true;
            entry._doc = head;

            return entry;
        }

    }

}

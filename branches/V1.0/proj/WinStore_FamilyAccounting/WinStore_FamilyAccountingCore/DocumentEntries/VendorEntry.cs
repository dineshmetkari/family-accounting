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
    public class VendorEntry : IDocumentEntry
    {
        public readonly static String VENDOR = "VENDOR";

        public readonly static String REC_ACC = "REC_ACC";

        public readonly static String GL_ACCOUNT = "GL_ACCOUNT";

        public readonly static String BUSINESS_AREA = "BUSINESS_AREA";

        private readonly CoreDriver _coreDriver;
        private readonly MasterDataManagement _mdMgmt;

        private MasterDataIdentity_GLAccount _recAcc;

        private MasterDataIdentity_GLAccount _glAccount;

        private MasterDataIdentity _vendor;

        private DateTime _date;

        private CurrencyAmount _amount;

        private String _text;

        private MasterDataIdentity _businessArea;

        private bool _isSaved;

        private HeadEntity _doc;

        public VendorEntry(CoreDriver coreDriver, MasterDataManagement mdMgmt)
        {
            _coreDriver = coreDriver;
            _mdMgmt = mdMgmt;
            _text = "";

            _recAcc = null;
            _glAccount = null;
            _vendor = null;
            _amount = new CurrencyAmount();
            _businessArea = null;

            _doc = null;
        }

        /// <summary>
        /// set vendor
        /// </summary>
        /// <param name="vendor"></param>
        /// <exception cref="NotInValueRangeException"></exception>
        private void setVendor(MasterDataIdentity vendor)
        {
            if (vendor == null)
            {
                throw new NotInValueRangeException(VENDOR, "");
            }

            if (!this._mdMgmt.ContainsMasterData(vendor, MasterDataType.VENDOR))
            {
                throw new NotInValueRangeException(VENDOR, vendor);
            }

            _vendor = vendor;
        }

        /// <summary>
        /// set gl account
        /// </summary>
        /// <param name="glAccount"></param>
        /// <exception cref="NotInValueRangeException"></exception>
        private void setGLAccount(MasterDataIdentity_GLAccount glAccount)
        {
            if (glAccount == null)
            {
                throw new NotInValueRangeException(GL_ACCOUNT, "");
            }

            List<MasterDataBase> valueSet = this.GetValueSet(GL_ACCOUNT);
            foreach (Object obj in valueSet)
            {
                GLAccountMasterData glAcc = (GLAccountMasterData)obj;
                if (glAcc.GLIdentity.Equals(glAccount))
                {
                    _glAccount = glAccount;
                    return;
                }
            }

            throw new NotInValueRangeException(GL_ACCOUNT, glAccount);
        }

        /// <summary>
        /// set business area
        /// </summary>
        /// <param name="businessArea"></param>
        /// <exception cref="NotInValueRangeException"></exception>
        private void setBusinessArea(MasterDataIdentity businessArea)
        {
            if (businessArea == null)
            {
                throw new NotInValueRangeException(BUSINESS_AREA, "");
            }

            if (!_mdMgmt.ContainsMasterData(businessArea,
                    MasterDataType.BUSINESS_AREA))
            {
                throw new NotInValueRangeException(BUSINESS_AREA, businessArea);
            }

            _businessArea = businessArea;
        }

        /// <summary>
        /// set recocciliation account
        /// </summary>
        /// <param name="recAcc"></param>
        /// <exception cref="NotInValueRangeException"></exception>
        private void setRecAccount(MasterDataIdentity_GLAccount recAcc)
        {
            if (recAcc == null)
            {
                throw new NotInValueRangeException(REC_ACC, "");
            }

            List<MasterDataBase> valueSet = this.GetValueSet(REC_ACC);
            foreach (Object obj in valueSet)
            {
                GLAccountMasterData glAccount = (GLAccountMasterData)obj;
                if (glAccount.GLIdentity.Equals(recAcc))
                {
                    _recAcc = recAcc;
                    return;
                }
            }

            throw new NotInValueRangeException(REC_ACC, recAcc);
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

            if (fieldName.Equals(VENDOR))
            {
                if (!(value is MasterDataIdentity))
                {
                    throw new NotInValueRangeException(fieldName, value);
                }
                MasterDataIdentity vendor = (MasterDataIdentity)value;
                setVendor(vendor);
            }
            else if (fieldName.Equals(GL_ACCOUNT))
            {
                if (!(value is MasterDataIdentity_GLAccount))
                {
                    throw new NotInValueRangeException(fieldName, value);
                }
                MasterDataIdentity_GLAccount glAccount = (MasterDataIdentity_GLAccount)value;
                setGLAccount(glAccount);
            }
            else if (fieldName.Equals(REC_ACC))
            {
                if (!(value is MasterDataIdentity_GLAccount))
                {
                    throw new NotInValueRangeException(fieldName, value);
                }
                MasterDataIdentity_GLAccount recAcc = (MasterDataIdentity_GLAccount)value;
                setRecAccount(recAcc);
            }
            else if (fieldName.Equals(EntryTemplate.POSTING_DATE))
            {
                if (!(value is DateTime))
                {
                    throw new NotInValueRangeException(fieldName, value);
                }
                DateTime date = (DateTime)value;
                _date = date;
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
            else if (fieldName.Equals(EntryTemplate.TEXT))
            {
                _text = value.ToString();
            }
            else if (fieldName.Equals(BUSINESS_AREA))
            {
                if (!(value is MasterDataIdentity))
                {
                    throw new NotInValueRangeException(fieldName, value);
                }
                MasterDataIdentity businessArea = (MasterDataIdentity)value;
                setBusinessArea(businessArea);
            }
            else
            {
                throw new NoFieldNameException(fieldName);
            }
        }


        /// <summary>
        /// 
        /// </summary>
        /// <param name="fieldName"></param>
        /// <returns></returns>
        /// <exception cref="NoFieldNameException"> no such field name</exception>
        public Object GetValue(String fieldName)
        {
            if (fieldName.Equals(VENDOR))
            {
                return _vendor;
            }
            else if (fieldName.Equals(GL_ACCOUNT))
            {
                return _glAccount;
            }
            else if (fieldName.Equals(REC_ACC))
            {
                return _recAcc;
            }
            else if (fieldName.Equals(EntryTemplate.POSTING_DATE))
            {
                return _date;
            }
            else if (fieldName.Equals(EntryTemplate.AMOUNT))
            {
                if (_amount == null || _amount.IsZero())
                {
                    return null;
                }
                return new CurrencyAmount(_amount);
            }
            else if (fieldName.Equals(EntryTemplate.TEXT))
            {
                return _text;
            }
            else if (fieldName.Equals(BUSINESS_AREA))
            {
                return _businessArea;
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
            // TODO Auto-generated method stub
            return null;
        }

        /// <summary>
        /// check before save
        /// </summary>
        /// <exception cref="MandatoryFieldIsMissing"></exception>
        public void CheckBeforeSave()
        {
            if (_recAcc == null)
            {
                throw new MandatoryFieldIsMissing(REC_ACC);
            }

            if (_glAccount == null)
            {
                throw new MandatoryFieldIsMissing(GL_ACCOUNT);
            }

            if (_vendor == null)
            {
                throw new MandatoryFieldIsMissing(VENDOR);
            }

            if (_date == null)
            {
                throw new MandatoryFieldIsMissing(EntryTemplate.POSTING_DATE);
            }

            if (_amount.IsZero() || _amount.IsNegative())
            {
                throw new MandatoryFieldIsMissing(EntryTemplate.AMOUNT);
            }

            if (_businessArea == null)
            {
                throw new MandatoryFieldIsMissing(BUSINESS_AREA);
            }
        }
        /// <summary>
        /// 
        /// </summary>
        /// <param name="saved"></param>
        /// <exception cref="MandatoryFieldIsMissing"></exception>
        /// <exception cref="SaveClosedLedgerException"></exception>
        public async Task SaveAsync(bool store)
        {
            if (_isSaved)
            {
                return;
            }

            // check before save
            CheckBeforeSave();

            try
            {
                HeadEntity doc = new HeadEntity(_coreDriver,
                        _coreDriver.MdMgmt);
                doc.SetDocText(_text);
                doc.SetDocumentType(DocumentType.VENDOR_INVOICE);
                doc.setPostingDate(_date);

                // credit item
                ItemEntity creditItem = doc.CreateEntity();
                creditItem.SetAmount(CreditDebitIndicator.CREDIT, _amount);
                creditItem.SetVendor(_vendor, _recAcc);

                // debit item
                ItemEntity debitItem = doc.CreateEntity();
                debitItem.SetAmount(CreditDebitIndicator.DEBIT, _amount);
                debitItem.SetGLAccount(_glAccount);
                debitItem.SetBusinessArea(_businessArea);


                await doc.SaveAsync(store);


                _isSaved = true;
                _doc = doc;

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

        public bool isSaved()
        {
            return _isSaved;
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
        /// 
        /// </summary>
        /// <param name="fieldName"></param>
        /// <returns></returns>
        /// <exception cref="NoFieldNameException"> no such field name</exception>
        public List<MasterDataBase> GetValueSet(String fieldName)
        {
            if (fieldName.Equals(GL_ACCOUNT))
            {
                return _mdMgmt.CostAccounts.ToList<MasterDataBase>();
            }
            else if (fieldName.Equals(REC_ACC))
            {
                return _mdMgmt.LiquidityAccounts.ToList<MasterDataBase>();
            }
            else if (fieldName.Equals(VENDOR))
            {
                MasterDataFactoryBase factory = _mdMgmt
                        .GetMasterDataFactory(MasterDataType.VENDOR);
                return factory.AllEntities;
            }
            else if (fieldName.Equals(BUSINESS_AREA))
            {
                MasterDataFactoryBase factory = _mdMgmt
                        .GetMasterDataFactory(MasterDataType.BUSINESS_AREA);
                return factory.AllEntities;
            }
            throw new NoFieldNameException(fieldName);
        }
        /// <summary>
        /// parse to vendor entry
        /// </summary>
        /// <param name="head"></param>
        /// <returns>return null if parse error</returns>
        public static VendorEntry Parse(HeadEntity head)
        {
            // check
            if (head.DocType != DocumentType.VENDOR_INVOICE)
            {
                return null;
            }
            List<ItemEntity> items = head.Items;
            if (items.Count != 2)
            {
                return null;
            }
            // credit item
            ItemEntity creditItem = items[0];
            if (creditItem.AccType != AccountType.VENDOR)
            {
                return null;
            }
            ItemEntity debitItem = items[1];
            if (debitItem.AccType != AccountType.GL_ACCOUNT)
            {
                return null;
            }

            VendorEntry entry = new VendorEntry(head._coreDriver, head._coreDriver.MdMgmt);
            entry._recAcc = creditItem.GLAccount;
            entry._vendor = creditItem.Vendor;
            entry._glAccount = debitItem.GLAccount;
            entry._businessArea = debitItem.BusinessArea;

            entry._date = head.PstDate;
            entry._amount = creditItem.Amount;
            if (creditItem.CdIndicator == CreditDebitIndicator.DEBIT)
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

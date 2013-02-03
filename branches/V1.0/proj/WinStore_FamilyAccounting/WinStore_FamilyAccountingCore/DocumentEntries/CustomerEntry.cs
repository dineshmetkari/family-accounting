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
    public class CustomerEntry : IDocumentEntry
    {
        public readonly static String CUSTOMER = "CUSTOMER";

        public readonly static String REC_ACC = "REC_ACC";

        public readonly static String GL_ACCOUNT = "GL_ACCOUNT";

        private readonly CoreDriver _coreDriver;
        private readonly MasterDataManagement _mdMgmt;

        private MasterDataIdentity_GLAccount _recAcc;

        private MasterDataIdentity_GLAccount _glAccount;

        private MasterDataIdentity _customer;

        private DateTime _date;

        private CurrencyAmount _amount;

        private String _text;

        private bool _isSaved;

        private HeadEntity _doc;

        /// <summary>
        /// constructor
        /// </summary>
        /// <param name="coreDriver"></param>
        public CustomerEntry(CoreDriver coreDriver, MasterDataManagement mdMgmt)
        {
            _coreDriver = coreDriver;
            _mdMgmt = mdMgmt;
        }

        /// <summary>
        /// set customer
        /// </summary>
        /// <param name="customer"></param>
        /// <exception cref="NotInValueRangeException"></exception>
        private void setCustomer(MasterDataIdentity customer)
        {
            if (customer == null)
            {
                throw new NotInValueRangeException(CUSTOMER, "");
            }
            if (!_mdMgmt.ContainsMasterData(customer, MasterDataType.CUSTOMER))
            {
                throw new NotInValueRangeException(CUSTOMER, customer);
            }

            _customer = customer;
        }

        /// <summary>
        /// set G/L account
        /// </summary>
        /// <param name="glAccount"></param>
        /// <exception cref="NotInValueRangeException"></exception>
        /// <exception cref="NoFieldNameException"></exception>
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
        /// set recconciliation account
        /// </summary>
        /// <param name="recAcc"></param>
        /// <exception cref="NotInValueRangeException"></exception>
        /// <exception cref="NoFieldNameException"></exception>
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
        /// Set value
        /// </summary>
        /// <param name="fieldName"></param>
        /// <param name="value"></param>
        /// <exception cref="NoFieldNameException"></exception>
        /// <exception cref="NotInValueRangeException"></exception>
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

            if (fieldName.Equals(CUSTOMER))
            {
                MasterDataIdentity customer = value as MasterDataIdentity;
                if (customer == null)
                {
                    throw new NotInValueRangeException(fieldName, value);
                }
                setCustomer(customer);
            }
            else if (fieldName.Equals(GL_ACCOUNT))
            {
                MasterDataIdentity_GLAccount glAccount = value as MasterDataIdentity_GLAccount;
                if (glAccount == null)
                {
                    throw new NotInValueRangeException(fieldName, value);
                }
                setGLAccount(glAccount);
            }
            else if (fieldName.Equals(REC_ACC))
            {
                MasterDataIdentity_GLAccount recAcc = value as MasterDataIdentity_GLAccount;
                if (recAcc == null)
                {
                    throw new NotInValueRangeException(fieldName, value);
                }
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
            else
            {
                throw new NoFieldNameException(fieldName);
            }

        }
        /// <summary>
        /// get value
        /// </summary>
        /// <param name="fieldName"></param>
        /// <returns></returns>
        /// <exception cref="NoFieldNameException"></exception>
        public Object GetValue(String fieldName)
        {
            if (fieldName.Equals(CUSTOMER))
            {
                return _customer;
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
            throw new NoFieldNameException(fieldName);
        }

        /// <summary>
        /// get default value
        /// </summary>
        /// <param name="fieldName"></param>
        /// <returns></returns>
        /// <exception cref="NoFieldNameException"></exception>
        public Object GetDefaultValue(String fieldName)
        {
            // TODO Auto-generated method stub
            return null;
        }

        /// <summary>
        /// check before saving
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

            if (_customer == null)
            {
                throw new MandatoryFieldIsMissing(CUSTOMER);
            }

            if (_date == null)
            {
                throw new MandatoryFieldIsMissing(EntryTemplate.POSTING_DATE);
            }

            if (_amount.IsZero() || _amount.IsNegative())
            {
                throw new MandatoryFieldIsMissing(EntryTemplate.AMOUNT);
            }

        }

        /// <summary>
        /// save document
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
                HeadEntity doc = new HeadEntity(_coreDriver,
                        _coreDriver.MdMgmt);
                doc.SetDocText(_text);
                doc.SetDocumentType(DocumentType.CUSTOMER_INVOICE);
                doc.setPostingDate(_date);

                // credit item
                ItemEntity creditItem = doc.CreateEntity();
                creditItem.SetAmount(CreditDebitIndicator.CREDIT, _amount);
                creditItem.SetGLAccount(_glAccount);

                // debit item
                ItemEntity debitItem = doc.CreateEntity();
                debitItem.SetAmount(CreditDebitIndicator.DEBIT, _amount);
                debitItem.SetCustomer(_customer, _recAcc);

                await doc.SaveAsync(store);


                _isSaved = true;
                _doc = doc;

            }
            catch (ArgumentNullException e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 267,
                        "Null value not acceptable.", MessageType.ERRO);
                throw new SystemException(e);
            }
            catch (MasterDataIdentityNotDefined e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 267,
                        "Master data identity is not defined.", MessageType.ERRO);
                throw new SystemException(e);
            }
            catch (BalanceNotZero e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 267,
                        "Balance is not zero.", MessageType.ERRO);
                throw new SystemException(e);
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
        /// get value set
        /// </summary>
        /// <param name="fieldName"></param>
        /// <returns></returns>
        /// <exception cref="NoFieldNameException"></exception>
        public List<MasterDataBase> GetValueSet(String fieldName)
        {
            if (fieldName.Equals(GL_ACCOUNT))
            {
                return _mdMgmt.RevenueAccounts.ToList<MasterDataBase>();
            }
            else if (fieldName.Equals(REC_ACC))
            {
                return _mdMgmt.LiquidityAccounts.ToList<MasterDataBase>();
            }
            else if (fieldName.Equals(CUSTOMER))
            {
                MasterDataFactoryBase factory = _mdMgmt
                        .GetMasterDataFactory(MasterDataType.CUSTOMER);
                return factory.AllEntities;
            }
            throw new NoFieldNameException(fieldName);
        }

        /// <summary>
        /// pasrse document to customer entry
        /// </summary>
        /// <param name="head"></param>
        /// <returns>return null, when cannot parse</returns>
        public static CustomerEntry parse(HeadEntity head, MasterDataManagement mdMgmt)
        {
            // check
            if (head.DocType != DocumentType.CUSTOMER_INVOICE)
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
            if (creditItem.AccType != AccountType.GL_ACCOUNT)
            {
                return null;
            }
            ItemEntity debitItem = items[1];
            if (debitItem.AccType != AccountType.CUSTOMER)
            {
                return null;
            }

            CustomerEntry entry = new CustomerEntry(head._coreDriver
                , head._coreDriver.MdMgmt);
            entry._glAccount = creditItem.GLAccount;
            entry._recAcc = debitItem.GLAccount;
            entry._customer = debitItem.Customer;
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

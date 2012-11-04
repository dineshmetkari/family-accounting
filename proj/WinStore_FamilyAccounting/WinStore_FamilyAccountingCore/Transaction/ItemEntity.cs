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
    public class ItemEntity : IComparable<ItemEntity>
    {
        #region Properties

        private readonly CoreDriver _coreDriver;
        public CoreDriver CoreDriver { get { return _coreDriver; } }
        private readonly MasterDataManagement _management;
        public MasterDataManagement MdMgmt { get { return _management; } }

        private readonly HeadEntity _head;
        public HeadEntity Header { get { return _head; } }

        private readonly int _lineNum;
        public int LineNum { get { return _lineNum; } }

        private MasterDataIdentity_GLAccount _glAccount;
        public MasterDataIdentity_GLAccount GLAccount { get { return _glAccount; } }

        private MasterDataIdentity _customer;
        public MasterDataIdentity Customer { get { return _customer; } }

        private MasterDataIdentity _vendor;
        public MasterDataIdentity Vendor { get { return _vendor; } }

        private CurrencyAmount _amount;
        public CurrencyAmount Amount { get { return new CurrencyAmount(_amount); } }

        private CreditDebitIndicator _cdIndicator;
        public CreditDebitIndicator CdIndicator { get { return _cdIndicator; } }

        private MasterDataIdentity _businessArea;
        public MasterDataIdentity BusinessArea { get { return _businessArea; } }

        internal bool _isSaved = false;

        /// <summary>
        /// Account type
        /// </summary>
        private AccountType _type;
        public AccountType AccType { get { return _type; } }

        #endregion

        /// <summary>
        /// Only internal can invoke
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="management"></param>
        /// <param name="head"></param>
        /// <param name="lineNum"></param>
        internal ItemEntity(CoreDriver coreDriver, MasterDataManagement management,
                HeadEntity head, int lineNum)
        {
            _coreDriver = coreDriver;
            _management = management;

            _head = head;
            _lineNum = lineNum;

            _glAccount = null;
            _customer = null;
            _vendor = null;
            _amount = new CurrencyAmount();
            _businessArea = null;
        }

        /// <summary>
        /// Set gl account
        /// </summary>
        /// <param name="glAccount"></param>
        /// <returns></returns>
        /// <exception cref="ArgumentNullException"></exception>
        /// <exception cref="MasterDataIdentityNotDefined"></exception>
        public bool SetGLAccount(MasterDataIdentity_GLAccount glAccount)
        {
            if (_isSaved)
            {
                return false;
            }

            // check G/L account
            if (glAccount == null)
            {
                throw new ArgumentNullException("G/L account");
            }
            MasterDataBase accountId = _management.GetMasterData(glAccount,
                    MasterDataType.GL_ACCOUNT);
            if (accountId == null)
            {
                throw new MasterDataIdentityNotDefined(glAccount,
                        MasterDataType.GL_ACCOUNT);
            }

            _type = AccountType.GL_ACCOUNT;
            _glAccount = glAccount;
            _vendor = null;
            _customer = null;

            return true;
        }

        /// <summary>
        /// Set customer
        /// </summary>
        /// <param name="glAccount">Outgoing Account</param>
        /// <param name="customer">Customer</param>
        /// <returns></returns>
        /// <exception cref="ArgumentNullException"></exception>
        /// <exception cref="MasterDataIdentityNotDefined"></exception>
        public bool SetCustomer(MasterDataIdentity customer,
                MasterDataIdentity_GLAccount glAccount)
        {
            if (_isSaved)
            {
                return false;
            }

            // check customer
            if (customer == null)
            {
                throw new ArgumentNullException("Customer");
            }
            MasterDataBase customerId = _management.GetMasterData(customer,
                    MasterDataType.CUSTOMER);
            if (customerId == null)
            {
                throw new MasterDataIdentityNotDefined(customer,
                        MasterDataType.CUSTOMER);
            }

            // check G/L account
            if (glAccount == null)
            {
                throw new ArgumentNullException("G/L account");
            }
            MasterDataIdentity accountId = _management.GetMasterData(glAccount,
                    MasterDataType.GL_ACCOUNT).Identity;
            if (accountId == null)
            {
                throw new MasterDataIdentityNotDefined(glAccount,
                        MasterDataType.GL_ACCOUNT);
            }

            _type = AccountType.CUSTOMER;
            _glAccount = glAccount;
            _customer = customer;
            _vendor = null;

            return true;
        }

        /// <summary>
        /// Set vendor
        /// </summary>
        /// <param name="vendor"></param>
        /// <param name="glAccount"></param>
        /// <returns></returns>
        /// <exception cref="ArgumentNullException"></exception>
        /// <exception cref="MasterDataIdentityNotDefined"></exception>
        public bool SetVendor(MasterDataIdentity vendor,
                MasterDataIdentity_GLAccount glAccount)
        {
            if (_isSaved)
            {
                return false;
            }

            // check customer
            if (vendor == null)
            {
                throw new ArgumentNullException("Vendor");
            }
            MasterDataBase vendorId = _management.GetMasterData(vendor,
                    MasterDataType.VENDOR);
            if (vendorId == null)
            {
                throw new MasterDataIdentityNotDefined(vendor,
                        MasterDataType.VENDOR);
            }

            // check G/L account
            if (glAccount == null)
            {
                throw new ArgumentNullException("G/L account");
            }
            MasterDataBase data = _management.GetMasterData(glAccount,
                    MasterDataType.GL_ACCOUNT);
            if (data == null)
            {
                throw new MasterDataIdentityNotDefined(glAccount,
                        MasterDataType.GL_ACCOUNT);
            }

            MasterDataIdentity accountId = data.Identity;
            if (accountId == null)
            {
                throw new MasterDataIdentityNotDefined(glAccount,
                        MasterDataType.GL_ACCOUNT);
            }

            _type = AccountType.VENDOR;
            _glAccount = glAccount;
            _vendor = vendor;
            _customer = null;

            return true;
        }

        /// <summary>
        /// Set amount
        /// </summary>
        /// <param name="indicator"></param>
        /// <param name="amount"></param>
        /// <returns></returns>
        public bool SetAmount(CreditDebitIndicator indicator,
                CurrencyAmount amount)
        {
            if (_isSaved)
            {
                return false;
            }

            if ( amount == null)
            {
                return false;
            }

            if (amount.IsNegative())
            {
                return false;
            }
            _cdIndicator = indicator;
            _amount.Set(amount);
            return true;
        }


        /// <summary>
        /// Set business area
        /// </summary>
        /// <param name="businessArea"></param>
        /// <returns></returns>
        /// <exception cref="MasterDataIdentityNotDefined"></exception>
        public bool SetBusinessArea(MasterDataIdentity businessArea)
        {
            if (_isSaved)
            {
                return false;
            }

            if (businessArea == null)
            {
                _businessArea = null;
                return true;
            }

            MasterDataBase accountId = _management.GetMasterData(businessArea,
                    MasterDataType.BUSINESS_AREA);
            if (accountId == null)
            {
                throw new MasterDataIdentityNotDefined(businessArea,
                        MasterDataType.BUSINESS_AREA);
            }

            _businessArea = businessArea;
            return true;
        }

        /// <summary>
        /// check wether mandatory fields is empty
        /// </summary>
        /// <exception cref="MandatoryFieldIsMissing"></exception>
        public void CheckMandatory()
        {
            // check account
            if (_type == AccountType.GL_ACCOUNT)
            {
                if (!(_glAccount != null && _customer == null && _vendor == null))
                {
                    _coreDriver
                            .logDebugInfo(
                                    this.GetType(),
                                    319,
                                    "Check line item before save, account error when account type is G/L account.",
                                    MessageType.ERRO);
                    throw new MandatoryFieldIsMissing("G/L Account");
                }
            }
            else if (_type == AccountType.CUSTOMER)
            {
                if (!(_glAccount != null && _customer != null && _vendor == null))
                {
                    _coreDriver
                            .logDebugInfo(
                                    this.GetType(),
                                    319,
                                    "Check line item before save, account error when account type is customer.",
                                    MessageType.ERRO);
                    throw new MandatoryFieldIsMissing("Customer");
                }
            }
            else
            {
                if (!(_glAccount != null && _customer == null && _vendor != null))
                {
                    _coreDriver
                            .logDebugInfo(
                                    this.GetType(),
                                    319,
                                    "Check line item before save, account error when account type is vendor.",
                                    MessageType.ERRO);
                    throw new MandatoryFieldIsMissing("Vendor");
                }
            }

            if (_amount.IsZero() || _amount.IsNegative())
            {
                _coreDriver.logDebugInfo(this.GetType(), 319,
                        "Check line item before save, amount <= 0.",
                        MessageType.ERRO);
                throw new MandatoryFieldIsMissing("Amount");
            }

            _coreDriver.logDebugInfo(this.GetType(), 319, String.Format(
                    "Check line item %d before save successfully", _lineNum),
                    MessageType.INFO);
        }

        /// <summary>
        /// parse XML to item entity
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="management"></param>
        /// <param name="head"></param>
        /// <param name="elem"></param>
        /// <returns></returns>
        /// <exception cref="TransactionDataFileFormatException"></exception>
        /// <exception cref="SystemException"></exception>
        public static ItemEntity Parse(CoreDriver coreDriver,
                MasterDataManagement management, HeadEntity head, XElement elem)
        {
            #region get line number
            XAttribute lineNumStr = elem.Attribute(TransDataUtils.XML_LINE_NUM);
            if (lineNumStr == null)
            {
                coreDriver.logDebugInfo(typeof(HeadEntity), 363, String.Format(
                        "Field {0} is missing in.", TransDataUtils.XML_LINE_NUM),
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            int lineNum;
            if (!Int32.TryParse(lineNumStr.Value, out lineNum))
            {
                coreDriver.logDebugInfo(typeof(HeadEntity), 363, String.Format(
                        "Format of field {0} is error.", TransDataUtils.XML_LINE_NUM),
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            #endregion

            #region get account type
            XAttribute typeStr = elem.Attribute(TransDataUtils.XML_ACCOUNT_TYPE);
            if (typeStr == null)
            {
                coreDriver
                        .logDebugInfo(typeof(HeadEntity), 375, String.Format(
                                "Field {0} is missing in.",
                                TransDataUtils.XML_ACCOUNT_TYPE), MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            if (typeStr.Value.Length != 1
                || (typeStr.Value[0] != (char)AccountType.CUSTOMER
                && typeStr.Value[0] != (char)AccountType.GL_ACCOUNT
                && typeStr.Value[0] != (char)AccountType.VENDOR))
            {
                coreDriver
                        .logDebugInfo(typeof(HeadEntity), 375, String.Format(
                                "Format of field {0} is error.",
                                TransDataUtils.XML_ACCOUNT_TYPE), MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            AccountType type = (AccountType)typeStr.Value[0];
            #endregion

            #region amount
            XAttribute amountStr = elem.Attribute(TransDataUtils.XML_AMOUNT);
            if (amountStr == null)
            {
                coreDriver.logDebugInfo(typeof(HeadEntity), 375, String.Format(
                        "Field {0} is missing in.", TransDataUtils.XML_AMOUNT),
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            CurrencyAmount amount;
            try
            {
                amount = CurrencyAmount.Parse(amountStr.Value);
            }
            catch (Exception e)
            {
                coreDriver
                        .logDebugInfo(typeof(HeadEntity), 375, e.Message, MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            #endregion


            #region credit debit indicator
            XAttribute cdIndStr = elem.Attribute(TransDataUtils.XML_CD_INDICATOR);
            if (cdIndStr == null)
            {
                coreDriver
                        .logDebugInfo(typeof(HeadEntity), 375, String.Format(
                                "Field {0} is missing in.",
                                TransDataUtils.XML_CD_INDICATOR), MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            if (cdIndStr.Value.Length != 1
                || (cdIndStr.Value[0] != (char)CreditDebitIndicator.CREDIT
                && cdIndStr.Value[0] != (char)CreditDebitIndicator.DEBIT))
            {
                coreDriver
                        .logDebugInfo(typeof(HeadEntity), 375, String.Format(
                                "Format of field {0} is error.",
                                TransDataUtils.XML_CD_INDICATOR), MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            CreditDebitIndicator indicator = (CreditDebitIndicator)cdIndStr.Value[0];
            #endregion

            #region G/L account
            XAttribute glAccountStr = elem.Attribute(TransDataUtils.XML_GL_ACCOUNT);
            if (glAccountStr == null)
            {
                coreDriver.logDebugInfo(typeof(HeadEntity), 414, String.Format(
                        "Field {0} is missing in.", TransDataUtils.XML_GL_ACCOUNT),
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            MasterDataIdentity_GLAccount glAccount;
            try
            {
                glAccount = new MasterDataIdentity_GLAccount(
                       glAccountStr.Value);
            }
            catch (Exception e)
            {
                coreDriver
                       .logDebugInfo(typeof(HeadEntity), 375, e.Message, MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            #endregion

            // vendor
            XAttribute vendorStr = elem.Attribute(TransDataUtils.XML_VENDOR);
            // customer
            XAttribute customerStr = elem.Attribute(TransDataUtils.XML_CUSTOMER);

            XAttribute businessAreaStr = elem
                    .Attribute(TransDataUtils.XML_BUSINESS_AREA);

            try
            {
                ItemEntity newItem = new ItemEntity(coreDriver, management, head,
                        lineNum);

                #region set account, vendor and customer
                if (type == AccountType.GL_ACCOUNT)
                {
                    newItem.SetGLAccount(glAccount);
                }
                else if (type == AccountType.VENDOR)
                {
                    if (vendorStr == null)
                    {
                        coreDriver.logDebugInfo(typeof(HeadEntity), 414, String
                                .Format("Field %s is missing in.",
                                        TransDataUtils.XML_VENDOR),
                                MessageType.ERRO);
                        throw new TransactionDataFileFormatException("");
                    }
                    MasterDataIdentity vendorId = new MasterDataIdentity(
                            vendorStr.Value);
                    newItem.SetVendor(vendorId, glAccount);
                }
                else if (type == AccountType.CUSTOMER)
                {
                    if (customerStr == null)
                    {
                        coreDriver.logDebugInfo(typeof(HeadEntity), 414, String
                                .Format("Field %s is missing in.",
                                        TransDataUtils.XML_CUSTOMER),
                                MessageType.ERRO);
                        throw new TransactionDataFileFormatException("");
                    }
                    MasterDataIdentity customerId = new MasterDataIdentity(
                            customerStr.Value);
                    newItem.SetCustomer(customerId, glAccount);
                }
                #endregion

                newItem.SetAmount(indicator, amount);

                if (businessAreaStr != null)
                {
                    newItem.SetBusinessArea(new MasterDataIdentity(businessAreaStr.Value));
                }

                coreDriver.logDebugInfo(
                        typeof(ItemEntity),
                        455,
                        String.Format("Parsed line Item {0}.", newItem.LineNum),
                        MessageType.INFO);
                return newItem;

            }
            catch (IdentityTooLong e)
            {
                coreDriver.logDebugInfo(typeof(ItemEntity), 463, e.Message,
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            catch (IdentityNoData e)
            {
                coreDriver.logDebugInfo(typeof(ItemEntity), 463, e.Message,
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            catch (IdentityInvalidChar e)
            {
                coreDriver.logDebugInfo(typeof(ItemEntity), 463, e.Message,
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            catch (ArgumentNullException e)
            {
                coreDriver.logDebugInfo(typeof(ItemEntity), 463, e.Message,
                        MessageType.ERRO);
                throw new SystemException(e);
            }
            catch (MasterDataIdentityNotDefined e)
            {
                coreDriver.logDebugInfo(typeof(ItemEntity), 463, e.Message,
                        MessageType.ERRO);
                throw new TransactionDataFileFormatException("");
            }
            catch (CurrencyAmountFormatException e)
            {
                throw new TransactionDataFileFormatException(e.Message);
            }

        }

        /// <summary>
        /// compare to 
        /// </summary>
        /// <param name="another"></param>
        /// <returns></returns>
        public int CompareTo(ItemEntity another)
        {
            return _lineNum - another._lineNum;
        }

        /// <summary>
        /// to xml
        /// </summary>
        /// <returns></returns>
        public XElement ToXml()
        {
            XElement xelem = new XElement(TransDataUtils.XML_ITEM);

            // line number
            xelem.Add(new XAttribute(TransDataUtils.XML_LINE_NUM, _lineNum.ToString()));

            // account type
            xelem.Add(new XAttribute(TransDataUtils.XML_ACCOUNT_TYPE, ((char)_type).ToString()));

            // g/l account
            xelem.Add(new XAttribute(TransDataUtils.XML_GL_ACCOUNT, _glAccount.ToString()));

            // customer
            if (_customer != null)
            {
                xelem.Add(new XAttribute(TransDataUtils.XML_CUSTOMER, _customer.ToString()));
            }

            // vendor
            if (_vendor != null)
            {
                xelem.Add(new XAttribute(TransDataUtils.XML_VENDOR, _vendor.ToString()));
            }

            // amount
            xelem.Add(new XAttribute(TransDataUtils.XML_AMOUNT, _amount.ToNumber()));

            // credit debit indicator
            xelem.Add(new XAttribute(TransDataUtils.XML_CD_INDICATOR, ((char)_cdIndicator).ToString()));

            // business area
            if (_businessArea != null)
            {
                xelem.Add(new XAttribute(TransDataUtils.XML_BUSINESS_AREA,
                                _businessArea.ToString()));
            }

            return xelem;
        }
    }

}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;
using WinStore_FamilyAccountingCore.Exceptions;
using WinStore_FamilyAccountingCore.Utilities;

namespace WinStore_FamilyAccountingCore.MasterData
{
    public class BankAccountMasterData : MasterDataBase
    {
        public static readonly String FILE_NAME = "bank_account.xml";

        /// <summary>
        /// bank account number
        /// </summary>
        private BankAccountNumber _accNumber;
        public BankAccountNumber AccountNumber
        {
            get { return _accNumber; }
        }
        /// <summary>
        /// bank key
        /// </summary>
        private MasterDataIdentity _bankKey;
        public MasterDataIdentity BankKey { get { return _bankKey; } }
        /// <summary>
        /// bank account type
        /// </summary>
        private BankAccountType _bankAccType;
        public BankAccountType BankAccType { get { return _bankAccType; } }

        /// <summary>
        /// Construct
        /// </summary>
        /// <param name="coreDriver">Core Driver</param>
        /// <param name="management">Matadata management</param>
        /// <param name="id"></param>
        /// <param name="descp"></param>
        /// <param name="accNumber"></param>
        /// <param name="bankKey"></param>
        /// <param name="type"></param>
        /// <exception cref="ArgumentNullException">Arguments null</exception>
        /// <exception cref="MasterDataIdentityNotDefined">Master data identity is not defined.</exception>
        public BankAccountMasterData(CoreDriver coreDriver, MasterDataManagement management,
                MasterDataIdentity id, String descp, BankAccountNumber accNumber,
                MasterDataIdentity bankKey, BankAccountType type)
            : base(coreDriver, management, id, descp)
        {
            _accNumber = accNumber;
            _bankAccType = type;

            MasterDataBase bankKeyId = management.GetMasterData(bankKey,
                    MasterDataType.BANK_KEY);
            if (bankKeyId == null)
            {
                throw new MasterDataIdentityNotDefined(bankKey,
                        MasterDataType.BANK_KEY);
            }
            _bankKey = bankKeyId.GetIdentity();
        }

        /// <summary>
        /// set bank account number
        /// </summary>
        /// <param name="accNum"></param>
        /// <exception cref="ArgumentNullException">Arguments null</exception>
        public void SetBankAccountNumber(BankAccountNumber accNum)
        {
            if (accNum == null)
            {
                throw new ArgumentNullException("Bank Account Number");
            }

            this.SetDirtyData();
            _accNumber = accNum;
        }

        /// <summary>
        /// set bank key
        /// </summary>
        /// <param name="bankKey"></param>
        /// <exception cref="ArgumentNullException">Arguments null</exception>
        /// <exception cref="MasterDataIdentityNotDefined">Master data identity is not defined.</exception>
        public void SetBankKey(MasterDataIdentity bankKey)
        {
            if (bankKey == null)
            {
                throw new ArgumentNullException("Bank Key");
            }

            MasterDataManagement management = this._management;
            MasterDataBase bankKeyId = management.GetMasterData(bankKey,
                    MasterDataType.BANK_KEY);
            if (bankKeyId == null)
            {
                throw new MasterDataIdentityNotDefined(bankKey,
                        MasterDataType.BANK_KEY);
            }

            this.SetDirtyData();
            _bankKey = bankKeyId.GetIdentity();
        }

        /// <summary>
        /// set bank account type
        /// </summary>
        /// <param name="bankKey"></param>
        public void setBankAccType(BankAccountType bankAccType)
        {
            this.SetDirtyData();
            _bankAccType = bankAccType;
        }

        /// <summary>
        /// parse memory to XML
        /// </summary>
        /// <returns></returns>
        public override XElement ToXML()
        {
            XElement xelem = base.ToXML();

            xelem.Add(new XAttribute(MasterDataUtils.XML_BANK_ACCOUNT, _accNumber.ToString()));
            xelem.Add(new XAttribute(MasterDataUtils.XML_BANK_KEY, _bankKey.ToString()));
            xelem.Add(new XAttribute(MasterDataUtils.XML_TYPE, ((char)_bankAccType).ToString()));
            return xelem;
        }
    }

}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;
using WinStore_FamilyAccountingCore.Exceptions;
using WinStore_FamilyAccountingCore.Exceptions.FormatExceptions;
using WinStore_FamilyAccountingCore.Utilities;

namespace WinStore_FamilyAccountingCore.MasterData
{
    public class BankAccountMasterDataFactory : MasterDataFactoryBase
    {
        /// <summary>
        /// Construct
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="management"></param>
        public BankAccountMasterDataFactory(CoreDriver coreDriver,
                MasterDataManagement management)
            : base(coreDriver, management)
        {
        }

        /// <summary>
        /// Create new master data 
        /// </summary>
        /// <param name="identity"></param>
        /// <param name="descp"></param>
        /// <param name="BankAccountNumber">Bank Account Number</param>
        /// <param name="MasterDataIdentity">Bank Key</param>
        /// <returns></returns>
        /// <exception cref="ParametersException">Parameters Exception</exception>
        /// <exception cref="MasterDataIdentityExists">Duplicated master data identity exists </exception>
        /// <exception cref="MasterDataIdentityNotDefined">Master data identity is not defined</exception>
        /// <exception cref="ArgumentNullException">Argument is null</exception>
        /// <exception cref="SystemException">Bug</exception>
        public override MasterDataBase CreateNewMasterDataBase(
                MasterDataIdentity identity, String descp, params Object[] objects)
        {
            if (identity == null || descp == null)
            {
                throw new ArgumentNullException();
            }
            if (objects.Length != 3)
            {
                throw new ParametersException(3, objects.Length);
            }

            // check identity is duplicated
            if (_list.ContainsKey(identity))
            {
                throw new MasterDataIdentityExists();
            }

            BankAccountNumber accNumber = null;
            MasterDataIdentity bankKey = null;
            BankAccountType type;

            accNumber = objects[0] as BankAccountNumber;
            if (accNumber == null)
            {
                throw new ParametersException();
            }

            bankKey = objects[1] as MasterDataIdentity;
            if (bankKey == null)
            {
                throw new ParametersException();
            }

            if (!(objects[2] is BankAccountType))
            {
                throw new ParametersException();
            }
            type = (BankAccountType)objects[2];

            try
            {
                BankAccountMasterData bankAccount = new BankAccountMasterData(_coreDriver, _management,
                            identity, descp, accNumber, bankKey, type);

                // add to list
                this._list.Add(identity, bankAccount);

                this._containDirtyData = true;

                // raise create master data
                _coreDriver.ListenerMgmt
                        .CreateMasterData(this, bankAccount);

                _coreDriver
                        .logDebugInfo(
                                this.GetType(),
                                84,
                                String.Format("Create bank account ({0}).",
                                        bankAccount.Identity.ToString()), MessageType.INFO);
                return bankAccount;
            }
            catch (Exception e)
            {// bug
                throw new SystemException(e);
            }
        }

        /// <summary>
        /// Parse master data from XML
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="elem"></param>
        /// <returns></returns>
        /// <exception cref="MasterDataFileFormatException">Master Data file exception</exception>
        /// <exception cref="ArgumentNullException">Argument is null</exception>
        /// <exception cref="SystemException">Bug</exception>
        public override MasterDataBase ParseMasterData(CoreDriver coreDriver,
                XElement elem)
        {
            XAttribute id = elem.Attribute(MasterDataUtils.XML_ID);
            XAttribute descp = elem.Attribute(MasterDataUtils.XML_DESCP);
            XAttribute bankKey = elem.Attribute(MasterDataUtils.XML_BANK_KEY);
            XAttribute bankAcc = elem.Attribute(MasterDataUtils.XML_BANK_ACCOUNT);
            XAttribute typeStr = elem.Attribute(MasterDataUtils.XML_TYPE);

            // check attribute
            if (bankKey == null)
            {
                _coreDriver.logDebugInfo(this.GetType(), 93, String.Format(
                        "Mandatory Field %s with no value",
                        MasterDataUtils.XML_BANK_KEY), MessageType.ERRO);
                throw new MasterDataFileFormatException(MasterDataType.BANK_ACCOUNT);
            }

            if (bankAcc == null)
            {
                _coreDriver.logDebugInfo(this.GetType(), 100, String.Format(
                        "Mandatory Field %s with no value",
                        MasterDataUtils.XML_BANK_ACCOUNT), MessageType.ERRO);
                throw new MasterDataFileFormatException(MasterDataType.BANK_ACCOUNT);
            }

            if (typeStr == null)
            {
                _coreDriver.logDebugInfo(this.GetType(), 114, String.Format(
                        "Mandatory Field %s with no value",
                        MasterDataUtils.XML_TYPE), MessageType.ERRO);
                throw new MasterDataFileFormatException(MasterDataType.BANK_ACCOUNT);
            }

            MasterDataIdentity identity;
            MasterDataIdentity bankKeyId;
            BankAccountNumber accNum;
            BankAccountType type;
            if (typeStr.Value.Length != 1
                || (typeStr.Value[0] != (char)BankAccountType.CREDIT_CARD
                && typeStr.Value[0] != (char)BankAccountType.SAVING_ACCOUNT))
            {
                _coreDriver.logDebugInfo(this.GetType(), 150,
                        "Format of bank account type is error.", MessageType.ERRO);
                throw new MasterDataFileFormatException(MasterDataType.BANK_ACCOUNT);
            }
            type = (BankAccountType)typeStr.Value[0];

            try
            {
                identity = new MasterDataIdentity(id.Value);
                // bank key
                bankKeyId = new MasterDataIdentity(
                        bankKey.Value);
                // bank account
                accNum = new BankAccountNumber(
                        bankAcc.Value);
            }
            catch (Exception e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 150,
                        e.Message, MessageType.ERRO);
                throw new MasterDataFileFormatException(MasterDataType.BANK_ACCOUNT);
            }


            try
            {
                BankAccountMasterData bankAccount = (BankAccountMasterData)this
                    .CreateNewMasterDataBase(identity, descp.Value, accNum,
                    bankKeyId, type);
                _coreDriver.logDebugInfo(
                    this.GetType(),
                    130,
                    String.Format("Parse bank account (%s).",
                            bankAccount.Identity.ToString()), MessageType.INFO);
                return bankAccount;
            }
            catch (Exception e)
            {
                // bug
                throw new SystemException(e);
            }

        }
    }
}

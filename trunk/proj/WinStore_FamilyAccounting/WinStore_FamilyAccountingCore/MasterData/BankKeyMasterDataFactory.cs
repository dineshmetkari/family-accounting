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
    public class BankKeyMasterDataFactory : MasterDataFactoryBase
    {
        /// <summary>
        /// 
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="management"></param>
        /// <exception cref="ArgumentNullException">Argument is null</exception>
        public BankKeyMasterDataFactory(CoreDriver coreDriver,
            MasterDataManagement management)
            : base(coreDriver, management)
        {
        }

        /// <summary>
        /// Create new master data 
        /// </summary>
        /// <param name="identity"></param>
        /// <param name="descp"></param>
        /// <param name="?"></param>
        /// <returns></returns>
        /// <exception cref="ParametersException">Parameters Exception</exception>
        /// <exception cref="MasterDataIdentityExists">Duplicated master data identity exists </exception>
        /// <exception cref="MasterDataIdentityNotDefined">Master data identity is not defined</exception>
        /// <exception cref="SystemException">Bug</exception>
        public override MasterDataBase CreateNewMasterDataBase(MasterDataIdentity id
            , string descp, params object[] objects)
        {
            if (id == null
                || descp == null)
            {
                throw new ArgumentNullException();
            }
            if (objects.Length > 0)
            {
                throw new ParametersException(0, objects.Length);
            }

            // check identity is duplicated
            if (_list.ContainsKey(id))
            {
                throw new MasterDataIdentityExists();
            }

            BankKeyMasterData bankKey = new BankKeyMasterData(_coreDriver, _management, id, descp);
            this._containDirtyData = true;
            this._list.Add(id, bankKey);

            // raise create master data
            _coreDriver.ListenerMgmt.CreateMasterData(this, bankKey);
            _coreDriver.logDebugInfo(this.GetType(), 47,
                    String.Format("Create bank key ({0}).", bankKey.Identity.ToString()),
                    MessageType.INFO);
            return bankKey;
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
        public override MasterDataBase ParseMasterData(CoreDriver coreDriver, XElement elem)
        {
            XAttribute idAttr = elem.Attribute(MasterDataUtils.XML_ID);
            XAttribute descpAttr = elem.Attribute(MasterDataUtils.XML_DESCP);

            MasterDataIdentity identity;
            try
            {
                identity = new MasterDataIdentity(idAttr.Value);
                BankKeyMasterData bankKey = (BankKeyMasterData)this
                        .CreateNewMasterDataBase(identity, descpAttr.Value);

                _coreDriver.logDebugInfo(this.GetType(), 61,
                        String.Format("Parse bank key ({0}).", bankKey.Identity.ToString()),
                        MessageType.INFO);
                return bankKey;
            }
            catch (IdentityTooLong e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 150, e.Message, MessageType.ERRO);
                throw new MasterDataFileFormatException(MasterDataType.BANK_KEY);
            }
            catch (IdentityNoData e)
            {
                _coreDriver
                        .logDebugInfo(this.GetType(), 154,
                                e.Message,
                                MessageType.ERRO);
                throw new MasterDataFileFormatException(MasterDataType.BANK_KEY);
            }
            catch (IdentityInvalidChar e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 160, e.Message, MessageType.ERRO);
                throw new MasterDataFileFormatException(MasterDataType.BANK_KEY);
            }
            catch (ParametersException e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 164,
                        "Function parameter set error: " + e.Message,
                        MessageType.ERRO);
                throw new SystemException(e);
            }
            catch (MasterDataIdentityExists e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 168, e.Message, MessageType.ERRO);
                throw new MasterDataFileFormatException(MasterDataType.BANK_KEY);
            }
        }
    }
}

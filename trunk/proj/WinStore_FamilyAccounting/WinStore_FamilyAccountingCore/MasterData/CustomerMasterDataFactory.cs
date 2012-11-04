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
    public class CustomerMasterDataFactory : MasterDataFactoryBase
    {
        /// <summary>
        /// 
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="management"></param>
        /// <exception cref="ArgumentNullException"></exception>
        public CustomerMasterDataFactory(CoreDriver coreDriver,
            MasterDataManagement management)
            : base(coreDriver, management)
        {
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="id"></param>
        /// <param name="descp"></param>
        /// <param name="?"></param>
        /// <returns></returns>
        /// <exception cref="ParametersException">Parameters Exception</exception>
        /// <exception cref="MasterDataIdentityExists">Duplicated master data identity exists </exception>
        /// <exception cref="MasterDataIdentityNotDefined">Master data identity is not defined</exception>
        /// <exception cref="ArgumentNullException">Argument is null</exception>
        /// <exception cref="SystemException">Bug</exception>
        public override MasterDataBase CreateNewMasterDataBase(MasterDataIdentity id,
                String descp, params Object[] objects)
        {
            // check parameters
            if (id == null
                || descp == null)
            {
                throw new ArgumentNullException();
            }
            if (objects.Length != 0)
            {
                throw new ParametersException(0, objects.Length);
            }

            // check identity is duplicated
            if (_list.ContainsKey(id))
            {
                throw new MasterDataIdentityExists();
            }

            CustomerMasterData customer = new CustomerMasterData(_coreDriver, _management, id, descp);

            this._containDirtyData = true;
            this._list.Add(id, customer);

            // raise create master data
            _coreDriver.ListenerMgmt.CreateMasterData(this, customer);
            _coreDriver.logDebugInfo(this.GetType(), 84,
                    String.Format("Create customer (%s).", customer.Identity.ToString()),
                    MessageType.INFO);
            return customer;
        }

        /// <summary>
        /// Parse master data from XElement
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="elem"></param>
        /// <returns></returns>
        /// <exception cref="MasterDataFileFormatException">Master Data file exception</exception>
        /// <exception cref="ArgumentNullException">Argument is null.</exception>
        /// <exception cref="SystemException">Bug</exception>
        public override MasterDataBase ParseMasterData(CoreDriver coreDriver, XElement elem)
        {
            if (coreDriver == null
                || elem == null)
            {
                throw new ArgumentNullException();
            }
            XAttribute idAttr = elem.Attribute(MasterDataUtils.XML_ID);
            XAttribute descpAttr = elem.Attribute(MasterDataUtils.XML_DESCP);

            try
            {
                MasterDataIdentity identity = new MasterDataIdentity(
                        idAttr.Value);

                CustomerMasterData customer = (CustomerMasterData)this
                        .CreateNewMasterDataBase(identity, descpAttr.Value);

                _coreDriver.logDebugInfo(this.GetType(), 130,
                        String.Format("Parse customer ({0}).", customer.Identity.ToString()),
                        MessageType.INFO);
                return customer;
            }
            catch (IdentityTooLong e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 150, e.Message, MessageType.ERRO);
                throw new MasterDataFileFormatException(MasterDataType.CUSTOMER);
            }
            catch (IdentityNoData e)
            {
                _coreDriver
                        .logDebugInfo(this.GetType(), 154, e.Message,
                                MessageType.ERRO);
                throw new MasterDataFileFormatException(MasterDataType.CUSTOMER);
            }
            catch (IdentityInvalidChar e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 160,
                       e.Message, MessageType.ERRO);
                throw new MasterDataFileFormatException(MasterDataType.CUSTOMER);
            }
            catch (ParametersException e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 164, e.Message,
                        MessageType.ERRO);
                throw new SystemException(e);
            }
            catch (MasterDataIdentityExists e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 168, e.Message, MessageType.ERRO);
                throw new MasterDataFileFormatException(MasterDataType.CUSTOMER);
            }

        }
    }
}

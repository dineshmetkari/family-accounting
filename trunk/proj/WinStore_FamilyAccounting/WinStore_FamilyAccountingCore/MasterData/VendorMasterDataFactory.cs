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
    public class VendorMasterDataFactory : MasterDataFactoryBase
    {
        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="coreDriver">Core Driver</param>
        /// <param name="management">Master data management</param>
        /// <exception cref="ArgumentNullException"></exception>
        public VendorMasterDataFactory(CoreDriver coreDriver,
            MasterDataManagement management)
            : base(coreDriver, management)
        {
        }

        /// <summary>
        /// create new master data
        /// </summary>
        /// <param name="identity"></param>
        /// <param name="descp"></param>
        /// <param name="objects"></param>
        /// <returns></returns>
        /// <exception cref="ParametersException">Parameters Exception</exception>
        /// <exception cref="MasterDataIdentityExists">Duplicated master data identity exists </exception>
        /// <exception cref="MasterDataIdentityNotDefined">Master data identity is not defined</exception>
        /// <exception cref="ArgumentNullException">Argument is null</exception>
        /// <exception cref="SystemException">Bug</exception>
        public override MasterDataBase CreateNewMasterDataBase(MasterDataIdentity identity
            , string descp, params object[] objects)
        {
            // check parameters
            if (identity == null ||
                descp == null)
            {
                throw new ArgumentNullException();
            }
            if (objects.Length != 0)
            {
                throw new ParametersException(0, objects.Length);
            }

            // check identity is duplicated
            if (_list.ContainsKey(identity))
            {
                throw new MasterDataIdentityExists();
            }

            VendorMasterData vendor = new VendorMasterData(_coreDriver, _management, identity, descp);
            this._containDirtyData = true;
            this._list.Add(identity, vendor);

            // raise create master data
            _coreDriver.ListenerMgmt.CreateMasterData(this, vendor);
            _coreDriver.logDebugInfo(this.GetType(), 84,
                    String.Format("Create vendor ({0}).", vendor.Identity.ToString()),
                    MessageType.INFO);
            return vendor;
        }

        /// <summary>
        /// Parse master data from XElement
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="elem"></param>
        /// <returns></returns>
        /// <exception cref="MasterDataFileFormatException">Master Data file exception</exception>
        /// <exception cref="SystemException">Bug</exception>
        /// <exception cref="ArgumentNullException">Argument is null.</exception>
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
                VendorMasterData vendor = (VendorMasterData)this
                        .CreateNewMasterDataBase(identity, descpAttr.Value);

                _coreDriver.logDebugInfo(this.GetType(), 62,
                        String.Format("Parse vendor ({0}).", vendor.Identity.ToString()),
                        MessageType.INFO);
                return vendor;
            }
            catch (IdentityTooLong e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 150, e.Message, MessageType.ERRO);
                throw new MasterDataFileFormatException(MasterDataType.VENDOR);
            }
            catch (IdentityNoData e)
            {
                _coreDriver
                        .logDebugInfo(this.GetType(), 154,
                                e.Message,
                                MessageType.ERRO);
                throw new MasterDataFileFormatException(MasterDataType.VENDOR);
            }
            catch (IdentityInvalidChar e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 160,
                        e.Message, MessageType.ERRO);
                throw new MasterDataFileFormatException(MasterDataType.VENDOR);
            }
            catch (ParametersException e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 164,
                        e.Message,
                        MessageType.ERRO);
                throw new SystemException(e);
            }
            catch (MasterDataIdentityExists e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 168,
                        e.Message, MessageType.ERRO);
                throw new MasterDataFileFormatException(MasterDataType.VENDOR);
            }
        }
    }
}

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
    public class BusinessAreaMasterDataFactory : MasterDataFactoryBase
    {
        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="management"></param>
        public BusinessAreaMasterDataFactory(CoreDriver coreDriver,
                MasterDataManagement management)
            : base(coreDriver, management)
        {
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="management"></param>
        /// <param name="id"></param>
        /// <param name="descp"></param>
        /// <param name="level"></param>
        /// <exception cref="ArgumentNullException">argument is null</exception>
        public override MasterDataBase CreateNewMasterDataBase(MasterDataIdentity identity,
                String descp, params Object[] objects)
        {
            if (objects.Length != 1)
            {
                throw new ParametersException(1, objects.Length);
            }

            // check identity is duplicated
            if (_list.ContainsKey(identity))
            {
                throw new MasterDataIdentityExists();
            }

            if (!(objects[0] is CriticalLevel))
            {
                throw new ParametersException();
            }
            CriticalLevel l = (CriticalLevel)objects[0];

            BusinessAreaMasterData businessArea;
            try
            {
                businessArea = new BusinessAreaMasterData(_coreDriver, _management, identity,
                        descp, l);
            }
            catch (ArgumentNullException e)
            {
                throw new SystemException(e);
            }

            this._containDirtyData = true;
            this._list.Add(identity, businessArea);

            // raise create master data
            _coreDriver.ListenerMgmt.CreateMasterData(this,
                    businessArea);
            _coreDriver.logDebugInfo(
                    this.GetType(),
                    59,
                    String.Format("Create business area (%s).",
                            businessArea.Identity.ToString()), MessageType.INFO);
            return businessArea;
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
            XAttribute id = elem.Attribute(MasterDataUtils.XML_ID);
            XAttribute descp = elem.Attribute(MasterDataUtils.XML_DESCP);
            XAttribute criticalLevel = elem
                    .Attribute(MasterDataUtils.XML_CRITICAL_LEVEL);
            // check attribute
            if (criticalLevel == null)
            {
                throw new MasterDataFileFormatException(
                        MasterDataType.BUSINESS_AREA);
            }
            if (criticalLevel.Value.Length != 1
                || (criticalLevel.Value[0] != (char)CriticalLevel.HIGH
                && criticalLevel.Value[0] != (char)CriticalLevel.MEDIUM
                && criticalLevel.Value[0] != (char)CriticalLevel.LOW))
            {
                _coreDriver
                        .logDebugInfo(this.GetType(), 154,
                                "Format of critical level is error",
                                MessageType.ERRO);
                throw new MasterDataFileFormatException(
                        MasterDataType.BUSINESS_AREA);
            }
            CriticalLevel l = (CriticalLevel)criticalLevel.Value[0];

            MasterDataIdentity identity;
            try
            {
                identity = new MasterDataIdentity(id.Value);
            }
            catch (Exception e)
            {
                _coreDriver
                        .logDebugInfo(this.GetType(), 154,
                                e.Message,
                                MessageType.ERRO);
                throw new MasterDataFileFormatException(
                        MasterDataType.BUSINESS_AREA);
            }

            try
            {
                BusinessAreaMasterData businessArea = (BusinessAreaMasterData)this
                            .CreateNewMasterDataBase(identity, descp.Value, l);

                _coreDriver.logDebugInfo(
                        this.GetType(),
                        130,
                        String.Format("Parse business area ({0}).",
                                businessArea.Identity.ToString()), MessageType.INFO);
                return businessArea;
            }
            catch (Exception e)
            {
                throw new SystemException(e);
            }

        }

    }
}

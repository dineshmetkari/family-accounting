using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace WinStore_FamilyAccountingCore.MasterData
{
    /// <summary>
    /// Base abstract class of master data
    /// </summary>
    public abstract class MasterDataBase : IComparable<MasterDataBase>
    {

        protected readonly MasterDataIdentity _identity;
        public MasterDataIdentity Identity { get { return _identity; } }
        protected String _descp; // description
        public String Descp { get { return _descp; } }
        protected readonly MasterDataManagement _management; // core driver
        protected readonly CoreDriver _coreDriver;

        /// <summary>
        /// Construct
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="management"></param>
        /// <param name="id"></param>
        /// <param name="descp"></param>
        /// <exception cref="ArgumentNullException">argument is null</exception>
        protected MasterDataBase(CoreDriver coreDriver,
                MasterDataManagement management, MasterDataIdentity id, String descp)
        {
            if (id == null)
            {
                throw new ArgumentNullException("Identity");
            }
            if (coreDriver == null)
            {
                throw new ArgumentNullException("CoreDriver");
            }
            if (management == null)
            {
                throw new ArgumentNullException("Management");
            }
            if (descp == null)
            {
                throw new ArgumentNullException("Description");
            }
            _coreDriver = coreDriver;
            _management = management;
            _identity = id;

            _descp = descp;

        }

        /// <summary>
        /// get identity of master data
        /// </summary>
        /// <returns></returns>
        public MasterDataIdentity GetIdentity()
        {
            return _identity;
        }

        /// <summary>
        /// set description
        /// </summary>
        /// <param name="descp"></param>
        /// <exception cref="ArgumentNullException">Argument is null</exception>
        public void SetDescp(String descp)
        {
            if (descp == null)
            {
                throw new ArgumentNullException("Desciption");
            }

            SetDirtyData();
            _descp = descp;
        }

        /// <summary>
        /// set dirty data
        /// </summary>
        protected void SetDirtyData()
        {
            MasterDataFactoryBase factory = null;
            if (this is BankAccountMasterData)
            {
                factory = _management
                        .GetMasterDataFactory(MasterDataType.BANK_ACCOUNT);
            }
            else if (this is BankKeyMasterData)
            {
                factory = _management.GetMasterDataFactory(MasterDataType.BANK_KEY);
            }
            else if (this is BusinessAreaMasterData)
            {
                factory = _management
                        .GetMasterDataFactory(MasterDataType.BUSINESS_AREA);
            }
            else if (this is CustomerMasterData)
            {
                factory = _management.GetMasterDataFactory(MasterDataType.CUSTOMER);
            }
            else if (this is GLAccountMasterData)
            {
                factory = _management
                        .GetMasterDataFactory(MasterDataType.GL_ACCOUNT);
            }
            else if (this is VendorMasterData)
            {
                factory = _management.GetMasterDataFactory(MasterDataType.VENDOR);
            }

            factory._containDirtyData = true;
        }

        /// <summary>
        /// To string
        /// </summary>
        /// <returns></returns>
        public override String ToString()
        {
            return this.Descp;
        }

        /// <summary>
        /// To XML
        /// </summary>
        /// <returns></returns>
        public virtual XElement ToXML()
        {
            XElement xelem = new XElement(MasterDataUtils.XML_ENTITY);
            xelem.Add(new XAttribute(MasterDataUtils.XML_ID, _identity.ToString()));
            xelem.Add(new XAttribute(MasterDataUtils.XML_DESCP, _descp));
            return xelem;
        }

        /// <summary>
        /// Comparable
        /// </summary>
        /// <param name="data"></param>
        /// <returns></returns>
        public int CompareTo(MasterDataBase data)
        {
            return this._identity.CompareTo(data._identity);
        }
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace WinStore_FamilyAccountingCore.MasterData
{
    /// <summary>
    /// Abstract Factory Pattern
    /// </summary>
    public abstract class MasterDataFactoryBase
    {
        protected readonly CoreDriver _coreDriver;

        protected readonly MasterDataManagement _management;

        protected readonly Dictionary<MasterDataIdentity, MasterDataBase> _list;
        public int MasterDataCount { get { return _list.Count; } }
        public List<MasterDataBase> AllEntities
        {
            get
            {
                List<MasterDataBase> ret = new List<MasterDataBase>(_list.Values.ToList<MasterDataBase>());
                ret.Sort();
                return ret;
            }
        }

        internal bool _containDirtyData;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="management"></param>
        /// <exception cref="ArgumentNullException">Argument is null</exception>
        protected MasterDataFactoryBase(CoreDriver coreDriver,
                MasterDataManagement management)
        {
            if (coreDriver == null
                || management == null)
            {
                throw new ArgumentNullException();
            }
            _coreDriver = coreDriver;
            _management = management;
            _list = new Dictionary<MasterDataIdentity, MasterDataBase>();
            _containDirtyData = false;
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
        /// <exception cref="ArgumentNullException">Argument is null</exception>
        /// <exception cref="SystemException">Bug</exception>
        public abstract MasterDataBase CreateNewMasterDataBase(
                MasterDataIdentity identity, String descp, params Object[] objects);

        /// <summary>
        /// Parse master data from XML
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="elem"></param>
        /// <returns></returns>
        /// <exception cref="MasterDataFileFormatException">Master Data file exception</exception>
        /// <exception cref="ArgumentNullException">Argument is null</exception>
        /// <exception cref="SystemException">Bug</exception>
        public abstract MasterDataBase ParseMasterData(CoreDriver coreDriver,
                XElement elem);

        /// <summary>
        /// To XML document
        /// </summary>
        /// <returns></returns>
        public XDocument ToXmlDocument()
        {
            XElement rootElem = new XElement(MasterDataUtils.XML_ROOT);
            XDocument xdoc = new XDocument(rootElem);

            foreach (MasterDataBase entity in this.AllEntities)
            {
                rootElem.Add(entity.ToXML());
            }

            return xdoc;
        }

        /// <summary>
        /// Get entity
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        /// <exception cref="ArgumentNullException">Argument is null.</exception>
        public MasterDataBase GetEntity(MasterDataIdentity id)
        {
            if (!Contains(id))
            {
                return null;
            }

            MasterDataBase md;
            _list.TryGetValue(id, out md);
            return md;
        }

        /// <summary>
        /// constains key
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        /// <exception cref="ArgumentNullException">Argument is null.</exception>
        public bool Contains(MasterDataIdentity id)
        {
            return _list.ContainsKey(id);
        }
    }

}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace WinStore_FamilyAccountingCore.MasterData
{
    public enum CriticalLevel
    {
        HIGH = 'H', MEDIUM = ('M'), LOW = ('L')
    }


    public class BusinessAreaMasterData : MasterDataBase
    {
        public static readonly String FILE_NAME = "business.xml";

        private CriticalLevel _criticalLevel;
        public CriticalLevel CriLevel { get { return _criticalLevel; } }
        /// <summary>
        /// 
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="management"></param>
        /// <param name="id"></param>
        /// <param name="descp"></param>
        /// <param name="level"></param>
        /// <exception cref="ArgumentNullException">argument is null</exception>
        public BusinessAreaMasterData(CoreDriver coreDriver, MasterDataManagement management, MasterDataIdentity id,
                String descp, CriticalLevel level)
            : base(coreDriver, management, id, descp)
        {
            _criticalLevel = level;
        }

        /// <summary>
        /// set critical level
        /// </summary>
        /// <param name="l"></param>
        public void SetCriticalLevel(CriticalLevel l)
        {
            this.SetDirtyData();
            _criticalLevel = l;
        }

        /// <summary>
        /// parse to XML
        /// </summary>
        /// <returns></returns>
        public override XElement ToXML()
        {
            XElement xelem = base.ToXML();
            xelem.Add(new XAttribute(MasterDataUtils.XML_CRITICAL_LEVEL
                , ((char)_criticalLevel).ToString()));
            return xelem;
        }
    }
}

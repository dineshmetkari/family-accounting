using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.MasterData
{
    public class BankKeyMasterData : MasterDataBase
    {
        public static readonly String FILE_NAME = "bank_key.xml";
        /// <summary>
        /// Construct
        /// </summary>
        /// <param name="coreDriver">Core Driver</param>
        /// <param name="management">Master Data management</param>
        /// <param name="id">Identity</param>
        /// <param name="descp">Description</param>
        /// <exception cref="ArgumentNullException">Arguments null</exception>
        public BankKeyMasterData(CoreDriver coreDriver, MasterDataManagement management, MasterDataIdentity id,
            String descp)
            : base(coreDriver, management, id, descp)
        {
        }
    }
}

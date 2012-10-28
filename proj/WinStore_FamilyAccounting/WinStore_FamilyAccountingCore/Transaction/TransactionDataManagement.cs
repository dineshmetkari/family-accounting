using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.MasterData;

namespace WinStore_FamilyAccountingCore.Transaction
{
    public class TransactionDataManagement : AbstractManagement
    {
        public TransactionDataManagement(CoreDriver coreDriver, MasterDataManagement mdMgmt)
            : base(coreDriver)
        {
        }

        public override void Initialize()
        {
            throw new NotImplementedException();
        }

        public override void Clear()
        {
            throw new NotImplementedException();
        }

        public override void EstablishFilesAsync()
        {
            throw new NotImplementedException();
        }
    }
}

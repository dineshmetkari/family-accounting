using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.MasterData;

namespace WinStore_FamilyAccountingCore.Reports
{
    public class ReportsManagement : AbstractManagement
    {
        public ReportsManagement(CoreDriver coreDriver, MasterDataManagement mdMgmt)
            : base(coreDriver)
        {
        }

        public override async Task InitializeAsync()
        {
        }

        public override void Clear()
        {
        }

        public override async Task EstablishFilesAsync()
        {
        }
    }
}

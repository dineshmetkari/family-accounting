using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions
{
    public class IdentityNoData : Exception
    {
        public IdentityNoData()
            : base("No data")
        {
        }
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions
{
    public class IdentityInvalidChar : Exception
    {
        public IdentityInvalidChar(char errorCh)
            : base(String.Format(
                "Master data identity cannot create with invalid charactor %c.",
                errorCh))
        {
        }
    }
}

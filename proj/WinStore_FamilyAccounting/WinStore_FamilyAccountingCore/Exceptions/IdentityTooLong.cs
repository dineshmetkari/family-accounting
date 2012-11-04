using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions
{
    public class IdentityTooLong : Exception
    {
        public IdentityTooLong(int actLen, int expLen)
            : base(String.Format("The length should short than {0}, but it is {1}",
                expLen, actLen))
        {
        }
    }
}

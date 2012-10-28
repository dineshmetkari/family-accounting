using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions
{
    public class FiscalMonthRangeException : Exception
    {
        public FiscalMonthRangeException(int fiscalMonth)
            : base(String.Format("Fiscal Year range is from 1 to 12. But it is %d.",
                fiscalMonth))
        {
        }
    }
}

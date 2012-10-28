using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions
{
    public class FiscalYearRangeException: Exception
    {
        public FiscalYearRangeException(int fiscalMonth)
            : base(String.Format("Fiscal Year range is from 1000 to 9999. But the value is %d",
                fiscalMonth))
        {
        }	
    }
}

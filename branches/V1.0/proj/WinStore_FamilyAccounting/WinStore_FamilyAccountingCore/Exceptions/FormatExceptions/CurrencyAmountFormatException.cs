using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions.FormatExceptions
{
    public class CurrencyAmountFormatException : FormatException
    {
        public CurrencyAmountFormatException(String value)
            : base(String.Format("Cannot parse {0} to CurrencyAmount.", value))
        {
        }
    }
}

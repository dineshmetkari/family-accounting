using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions.FormatExceptions
{
    public abstract class AbstractFormatException : Exception
    {
        public AbstractFormatException(string message)
            : base(message)
        {
        }
    }
}

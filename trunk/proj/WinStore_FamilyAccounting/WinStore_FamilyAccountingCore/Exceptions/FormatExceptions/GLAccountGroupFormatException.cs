using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions.FormatExceptions
{
    public class GLAccountGroupFormatException : Exception
    {
        public GLAccountGroupFormatException(String value)
            : base(value + " cannot be parse to GLAccountGroup")
        {
        }
    }
}

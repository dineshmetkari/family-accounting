using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions.FormatExceptions
{
    public class MetaDataFormatException : AbstractFormatException
    {
        public MetaDataFormatException(string msg)
            : base(msg)
        {
        }
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions
{
    public class NoFieldNameException : Exception
    {
        public NoFieldNameException(String fieldName)
            : base("No such field " + fieldName)
        {
        }
    }
}

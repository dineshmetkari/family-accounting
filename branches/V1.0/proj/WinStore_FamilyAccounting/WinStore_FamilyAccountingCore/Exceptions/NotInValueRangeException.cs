using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions
{
    public class NotInValueRangeException : Exception
    {
        public NotInValueRangeException(String fieldName, Object obj)
            : base(String.Format("Value {0} is not in value range {1}.",
                    obj.ToString(), fieldName))
        {
        }
    }
}

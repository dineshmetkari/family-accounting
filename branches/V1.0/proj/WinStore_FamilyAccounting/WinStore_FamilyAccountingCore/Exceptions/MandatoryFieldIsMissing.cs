using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions
{
    public class MandatoryFieldIsMissing : Exception
    {
        public String FieldName { get; protected set; }
        public MandatoryFieldIsMissing(String fieldName)
            : base(String.Format("Mandatory field {0} is missing", fieldName))
        {
            FieldName = fieldName;
        }
    }
}

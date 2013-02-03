using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions.FormatExceptions
{
    public class DocumentIdentityFormatException : Exception
    {
        public DocumentIdentityFormatException(String docIdStr)
            : base(String.Format("Parsing document Identity \"{0}\" contains error.",
                    docIdStr))
        {
        }
    }
}

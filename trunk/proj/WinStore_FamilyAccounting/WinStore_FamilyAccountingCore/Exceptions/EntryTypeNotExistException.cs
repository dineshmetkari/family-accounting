using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions
{
    public class EntryTypeNotExistException : Exception
    {
        public EntryTypeNotExistException()
            : base("Entry type not exists.")
        {
        }
    }
}

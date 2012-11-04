using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions
{
    public class ReverseOrgDocNotExistException : Exception
    {
        public ReverseOrgDocNotExistException()
            : base("Document to be reversed does not exist")
        {
        }
    }
}

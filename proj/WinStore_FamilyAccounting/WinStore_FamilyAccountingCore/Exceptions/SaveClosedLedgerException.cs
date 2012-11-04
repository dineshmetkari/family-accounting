using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions
{
   public class SaveClosedLedgerException : Exception
    {
       public SaveClosedLedgerException()
           : base("Cannot save document or modify document in open ledger.")
       {
       }
    }
}

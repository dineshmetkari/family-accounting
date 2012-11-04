using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions.FormatExceptions
{
    public class TransactionDataFileFormatException : Exception
    {
        public String FilePath { get; protected set; }

        public TransactionDataFileFormatException(String filePath)
            : base(String.Format("Transaction data file {0} contains format error",
                    filePath))
        {
            FilePath = filePath;
        }
    }
}

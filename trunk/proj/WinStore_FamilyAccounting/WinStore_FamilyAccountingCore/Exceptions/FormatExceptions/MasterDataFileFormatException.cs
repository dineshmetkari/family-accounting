using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.MasterData;

namespace WinStore_FamilyAccountingCore.Exceptions.FormatExceptions
{
    public class MasterDataFileFormatException : FormatException
    {
        public readonly MasterDataType TYPE;

        public MasterDataFileFormatException(MasterDataType type)
            : base(String.Format(
                    "Master data file for master data %s contains format error",
                    type))
        {
            TYPE = type;
        }
    }
}
